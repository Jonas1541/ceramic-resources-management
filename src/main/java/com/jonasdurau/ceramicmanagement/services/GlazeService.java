package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.GlazeListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeResourceUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeMachineUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeResourceUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Glaze;
import com.jonasdurau.ceramicmanagement.entities.GlazeMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeResourceUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeTransaction;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;
import com.jonasdurau.ceramicmanagement.repositories.GlazeMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeResourceUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class GlazeService implements IndependentCrudService<GlazeListDTO, GlazeRequestDTO, GlazeResponseDTO, Long> {

    @Autowired
    private GlazeRepository glazeRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private GlazeTransactionRepository transactionRepository;

    @Autowired
    private GlazeResourceUsageRepository glazeResourceUsageRepository;

    @Autowired
    private GlazeMachineUsageRepository glazeMachineUsageRepository;

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<GlazeListDTO> findAll() {
        List<Glaze> entities = glazeRepository.findAll();
        return entities.stream()
            .map(glaze -> new GlazeListDTO(
                glaze.getId(),
                glaze.getCreatedAt(),
                glaze.getUpdatedAt(),
                glaze.getColor(),
                glaze.getUnitCost(),
                glaze.getCurrentQuantity(),
                glaze.getCurrentQuantityPrice()
            ))
            .toList();
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public GlazeResponseDTO findById(Long id) {
        Glaze glaze = glazeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze not found: " + id));
        return entityToResponseDTO(glaze);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public GlazeResponseDTO create(GlazeRequestDTO dto) {
        Glaze glaze = new Glaze();
        glaze.setColor(dto.color());
        for (GlazeResourceUsageRequestDTO usageDTO : dto.resourceUsages()) {
            Resource resource = resourceRepository.findById(usageDTO.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + usageDTO.resourceId()));
            GlazeResourceUsage usage = new GlazeResourceUsage();
            usage.setGlaze(glaze);
            usage.setResource(resource);
            usage.setQuantity(usageDTO.quantity());
            glaze.getResourceUsages().add(usage);
        }
        for (GlazeMachineUsageRequestDTO muDTO : dto.machineUsages()) {
            Machine machine = machineRepository.findById(muDTO.machineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.machineId()));
            GlazeMachineUsage mu = new GlazeMachineUsage();
            mu.setGlaze(glaze);
            mu.setMachine(machine);
            mu.setUsageTime(muDTO.usageTime());
            glaze.getMachineUsages().add(mu);
        }
        computeUnitCost(glaze);
        glaze = glazeRepository.save(glaze);
        return entityToResponseDTO(glaze);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public GlazeResponseDTO update(Long id, GlazeRequestDTO dto) {
        Glaze glaze = glazeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze not found: " + id));
        Map<Long, GlazeResourceUsage> existingResourceUsages = glaze.getResourceUsages().stream()
            .collect(Collectors.toMap(r -> r.getResource().getId(), r -> r));
        for (GlazeResourceUsageRequestDTO usageDTO : dto.resourceUsages()) {
            GlazeResourceUsage existingUsage = existingResourceUsages.get(usageDTO.resourceId());
            if (existingUsage != null) {
                existingUsage.setQuantity(usageDTO.quantity());
                existingResourceUsages.remove(usageDTO.resourceId());
            } else {
                Resource resource = resourceRepository.findById(usageDTO.resourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + usageDTO.resourceId()));
                GlazeResourceUsage newUsage = new GlazeResourceUsage();
                newUsage.setGlaze(glaze);
                newUsage.setResource(resource);
                newUsage.setQuantity(usageDTO.quantity());
                glaze.getResourceUsages().add(newUsage);
            }
        }
        for (GlazeResourceUsage removedUsage : existingResourceUsages.values()) {
            glaze.getResourceUsages().remove(removedUsage);
        }
        Map<Long, GlazeMachineUsage> existingMachineUsages = glaze.getMachineUsages().stream()
            .collect(Collectors.toMap(mu -> mu.getMachine().getId(), mu -> mu));
        Set<Long> updatedMachineIds = new HashSet<>();
        for (GlazeMachineUsageRequestDTO muDTO : dto.machineUsages()) {
            GlazeMachineUsage existingMu = existingMachineUsages.get(muDTO.machineId());
            if (existingMu != null) {
                existingMu.setUsageTime(muDTO.usageTime());
                updatedMachineIds.add(muDTO.machineId());
            } else {
                Machine machine = machineRepository.findById(muDTO.machineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.machineId()));
                GlazeMachineUsage newMu = new GlazeMachineUsage();
                newMu.setGlaze(glaze);
                newMu.setMachine(machine);
                newMu.setUsageTime(muDTO.usageTime());
                glaze.getMachineUsages().add(newMu);
                updatedMachineIds.add(muDTO.machineId());
            }
        }
        List<GlazeMachineUsage> muToRemove = glaze.getMachineUsages().stream()
            .filter(mu -> !updatedMachineIds.contains(mu.getMachine().getId()))
            .collect(Collectors.toList());
        for (GlazeMachineUsage mu : muToRemove) {
            glaze.getMachineUsages().remove(mu);
        }
        glaze.setColor(dto.color());
        computeUnitCost(glaze);
        glaze = glazeRepository.save(glaze);
        return entityToResponseDTO(glaze);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public void delete(Long id) {
        Glaze glaze = glazeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze not found: " + id));
        boolean hasTransactions = transactionRepository.existsByGlazeId(id);
        if (hasTransactions) {
            throw new ResourceDeletionException("Não é possível deletar a glaze com id " 
                + id + " pois ela possui transações associadas.");
        }
        glazeRepository.delete(glaze);
    }

    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<YearReportDTO> yearlyReport(Long glazeId) {
        Glaze glaze = glazeRepository.findById(glazeId)
                .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrada: " + glazeId));
        List<GlazeTransaction> txs = glaze.getTransactions();
        ZoneId zone = ZoneId.systemDefault();
        Map<Integer, Map<Month, List<GlazeTransaction>>> mapYearMonth = txs.stream()
                .map(t -> new AbstractMap.SimpleEntry<>(t, t.getCreatedAt().atZone(zone)))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getValue().getMonth(),
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<GlazeTransaction>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<GlazeTransaction>> mapMonth = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            double totalOutgoingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            BigDecimal totalOutgoingProfitYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<GlazeTransaction> monthTx = mapMonth.getOrDefault(m, Collections.emptyList());
                double incomingQty = 0.0;
                double outgoingQty = 0.0;
                BigDecimal incomingCost = BigDecimal.ZERO;
                BigDecimal outgoingProfit = BigDecimal.ZERO;
                for (GlazeTransaction t : monthTx) {
                    if (t.getType() == TransactionType.INCOMING) {
                        incomingQty += t.getQuantity();
                        incomingCost = incomingCost.add(t.getGlazeFinalCostAtTime());
                    } else {
                        outgoingQty += t.getQuantity();
                    }
                }
                totalIncomingQtyYear += incomingQty;
                totalOutgoingQtyYear += outgoingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
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

    private GlazeResponseDTO entityToResponseDTO(Glaze entity) {
        List<GlazeResourceUsageResponseDTO> resourceUsageDTOs = entity.getResourceUsages().stream()
            .map(usage -> {
                return new GlazeResourceUsageResponseDTO(
                    usage.getResource().getId(),
                    usage.getResource().getName(),
                    usage.getQuantity()
                );
            })
            .collect(Collectors.toList());
        List<GlazeMachineUsageResponseDTO> machineUsageDTOs = entity.getMachineUsages().stream()
            .map(mu -> {
                return new GlazeMachineUsageResponseDTO(
                    mu.getMachine().getId(),
                    mu.getMachine().getName(),
                    mu.getUsageTime()
                );
            })
            .collect(Collectors.toList());
        return new GlazeResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getColor(),
            resourceUsageDTOs,
            machineUsageDTOs,
            entity.getUnitCost(),
            entity.getCurrentQuantity(),
            entity.getCurrentQuantityPrice()
        );
    }

    private void computeUnitCost(Glaze glaze) {
        BigDecimal resourceCost = BigDecimal.ZERO;
        for (GlazeResourceUsage usage : glaze.getResourceUsages()) {
            BigDecimal resourceUnitValue = usage.getResource().getUnitValue(); 
            BigDecimal subCost = resourceUnitValue.multiply(BigDecimal.valueOf(usage.getQuantity()));
            resourceCost = resourceCost.add(subCost);
        }
        BigDecimal machineCost = computeMachineCost(glaze);
        BigDecimal finalCost = resourceCost.add(machineCost).setScale(2, RoundingMode.HALF_UP);
        glaze.setUnitCost(finalCost);
    }

    private BigDecimal computeMachineCost(Glaze glaze) {
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
            .orElseThrow(() -> new BusinessException("ELECTRICITY resource not found"));
        BigDecimal total = BigDecimal.ZERO;
        for (GlazeMachineUsage mu : glaze.getMachineUsages()) {
            double kwh = mu.getEnergyConsumption(); // e.g. from getEnergyConsumption() logic
            BigDecimal cost = electricity.getUnitValue().multiply(BigDecimal.valueOf(kwh));
            total = total.add(cost);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public void recalculateGlazesByResource(Long resourceId) {
        List<GlazeResourceUsage> usages = glazeResourceUsageRepository.findByResourceId(resourceId);
        List<Glaze> glazes = usages.stream()
            .map(GlazeResourceUsage::getGlaze)
            .distinct()
            .collect(Collectors.toList());
        for (Glaze glaze : glazes) {
            computeUnitCost(glaze);
            glazeRepository.save(glaze);
        }
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public void recalculateGlazesByMachine(Long machineId) {
        List<GlazeMachineUsage> usages = glazeMachineUsageRepository.findByMachineId(machineId);
        List<Glaze> glazes = usages.stream()
                .map(GlazeMachineUsage::getGlaze)
                .distinct()
                .collect(Collectors.toList());
        for (Glaze g : glazes) {
            computeUnitCost(g);
            glazeRepository.save(g);
        }
    }
}
