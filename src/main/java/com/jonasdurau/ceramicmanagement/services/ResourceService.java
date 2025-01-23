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

        // Carrega as transações associadas ao resource (ou use
        // repository.findByResource(resource))
        List<ResourceTransaction> txs = resource.getTransactions();

        // Passo 1: agrupar por (year, month)
        Map<Integer, Map<Month, List<ResourceTransaction>>> mapYearMonth = txs.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCreatedAt().atZone(ZoneId.systemDefault()).getYear(),
                        Collectors.groupingBy(t -> t.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .getMonth())));

        // Passo 2: percorrer mapYearMonth e montar YearReportDTO para cada ano
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<ResourceTransaction>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<ResourceTransaction>> mapMonth = yearEntry.getValue();

            YearReportDTO yearReport = new YearReportDTO(year);

            double totalIncomingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            double totalOutgoingQtyYear = 0.0;

            // Passo 2.1: percorrer os 12 meses (Month.values())
            for (Month m : Month.values()) {
                // se quiser evitar meses “vazios”, você pode iterar só sobre mapMonth.keySet()
                List<ResourceTransaction> monthTx = mapMonth.getOrDefault(m, Collections.emptyList());
                if (monthTx.isEmpty()) {
                    // se quiser pular meses sem transações, continue
                    // ou criar MonthReportDTO com zero
                }

                double incomingQty = 0.0;
                BigDecimal incomingCost = BigDecimal.ZERO;
                double outgoingQty = 0.0;

                // Soma as transações do mês
                for (ResourceTransaction t : monthTx) {
                    if (t.getType() == TransactionType.INCOMING) {
                        incomingQty += t.getQuantity();
                        // cost = unitValue * quantity (ou t.getCost() se for calculado)
                        BigDecimal cost = resource.getUnitValue().multiply(BigDecimal.valueOf(t.getQuantity()));
                        incomingCost = incomingCost.add(cost);
                    } else {
                        // OUTGOING
                        outgoingQty += t.getQuantity();
                    }
                }

                // Atualiza total anual
                totalIncomingQtyYear += incomingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
                totalOutgoingQtyYear += outgoingQty;

                // Cria o MonthReportDTO
                MonthReportDTO monthDto = new MonthReportDTO();
                monthDto.setMonthName(m.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                monthDto.setIncomingQty(incomingQty);
                monthDto.setIncomingCost(incomingCost);
                monthDto.setOutgoingQty(outgoingQty);

                yearReport.getMonths().add(monthDto);
            }

            // Passo 2.2: define os totais anuais no YearReportDTO
            yearReport.setTotalIncomingQty(totalIncomingQtyYear);
            yearReport.setTotalIncomingCost(totalIncomingCostYear);
            yearReport.setTotalOutgoingQty(totalOutgoingQtyYear);

            yearReports.add(yearReport);
        }

        // Você pode ordenar yearReports por ano decrescente se quiser
        yearReports.sort((a, b) -> b.getYear() - a.getYear());

        return yearReports;
    }

}
