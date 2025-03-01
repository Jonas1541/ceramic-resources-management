package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.ResourceDTO;
import com.jonasdurau.ceramicmanagement.dtos.ResourceListDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;
import com.jonasdurau.ceramicmanagement.repositories.BatchResourceUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeResourceUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTransactionRepository transactionRepository;

    @Autowired
    private BatchResourceUsageRepository batchResourceUsageRepository;

    @Autowired
    private GlazeResourceUsageRepository glazeResourceUsageRepository;

    @Autowired
    private GlazeService glazeService;

    @Transactional(readOnly = true)
    public List<ResourceListDTO> findAll() {
        List<Resource> list = resourceRepository.findAll();
        return list.stream()
                .map(r -> {
                    double currentQty = r.getCurrentQuantity();
                    BigDecimal currentPrice = r.getCurrentQuantityPrice();

                    return new ResourceListDTO(
                            r.getId(),
                            r.getName(),
                            r.getCategory().name(),
                            currentQty,
                            currentPrice);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ResourceDTO findById(Long id) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public ResourceDTO create(ResourceDTO dto) {
        if (resourceRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome '" + dto.getName() + "' já existe.");
        }
        Resource entity = dtoToEntity(dto);
        entity = resourceRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public ResourceDTO update(Long id, ResourceDTO dto) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        String newName = dto.getName();
        String oldName = entity.getName();
        if (!oldName.equals(newName) && resourceRepository.existsByName(newName)) {
            throw new BusinessException("O nome '" + newName + "' já existe.");
        }
        entity.setName(newName);
        entity.setCategory(dto.getCategory());
        entity.setUnitValue(dto.getUnitValue());
        entity = resourceRepository.save(entity);
        glazeService.recalculateGlazesByResource(id);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        boolean hasTransactions = transactionRepository.existsByResourceId(id);
        boolean hasBatchUsages = batchResourceUsageRepository.existsByResourceId(id);
        boolean hasGlazeUsages = glazeResourceUsageRepository.existsByResourceId(id);
        if (hasTransactions) {
            throw new ResourceDeletionException("Não é possível deletar o recurso com id " + id + " pois ele tem transações associadas.");
        }
        if (hasBatchUsages) {
            throw new ResourceDeletionException("Não é possível deletar o recurso com id " + id + " pois ele tem bateladas associadas.");
        }
        if (hasGlazeUsages) {
            throw new ResourceDeletionException("Não é possível deletar o recurso com id " + id + " pois ele tem glasuras associadas.");
        }
        resourceRepository.delete(entity);
    }

    private ResourceDTO entityToDTO(Resource entity) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setUnitValue(entity.getUnitValue());
        dto.setCurrentQuantity(entity.getCurrentQuantity());
        dto.setCurrentQuantityPrice(entity.getCurrentQuantityPrice());
        return dto;
    }

    private Resource dtoToEntity(ResourceDTO dto) {
        Resource entity = new Resource();
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setUnitValue(dto.getUnitValue());
        return entity;
    }

    @Transactional
    public List<YearReportDTO> getYearlyReport(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceId));
        List<ResourceTransaction> txs = resource.getTransactions();
        ZoneId zone = ZoneId.systemDefault();
        Map<Integer, Map<Month, List<ResourceTransaction>>> mapYearMonth = txs.stream()
                .map(t -> new AbstractMap.SimpleEntry<>(t, t.getCreatedAt().atZone(zone)))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getValue().getMonth(),
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<ResourceTransaction>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<ResourceTransaction>> mapMonth = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            double totalOutgoingQtyYear = 0.0;
            BigDecimal totalOutgoingProfitYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<ResourceTransaction> monthTx = mapMonth.getOrDefault(m, Collections.emptyList());
                double incomingQty = 0.0;
                BigDecimal incomingCost = BigDecimal.ZERO;
                double outgoingQty = 0.0;
                BigDecimal outgoingProfit = BigDecimal.ZERO;
                for (ResourceTransaction t : monthTx) {
                    if (t.getType() == TransactionType.INCOMING) {
                        incomingQty += t.getQuantity();
                        BigDecimal cost = resource.getUnitValue().multiply(BigDecimal.valueOf(t.getQuantity()));
                        incomingCost = incomingCost.add(cost);
                    } else {
                        outgoingQty += t.getQuantity();
                    }
                }
                totalIncomingQtyYear += incomingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
                totalOutgoingQtyYear += outgoingQty;
                MonthReportDTO monthDto = new MonthReportDTO();
                monthDto.setMonthName(m.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                monthDto.setIncomingQty(incomingQty);
                monthDto.setIncomingCost(incomingCost);
                monthDto.setOutgoingQty(outgoingQty);
                monthDto.setOutgoingProfit(outgoingProfit);
                yearReport.getMonths().add(monthDto);
            }
            yearReport.setTotalIncomingQty(totalIncomingQtyYear);
            yearReport.setTotalIncomingCost(totalIncomingCostYear);
            yearReport.setTotalOutgoingQty(totalOutgoingQtyYear);
            yearReport.setTotalOutgoingProfit(totalOutgoingProfitYear);
            yearReports.add(yearReport);
        }
        yearReports.sort((a, b) -> b.getYear() - a.getYear());
        return yearReports;
    }
}
