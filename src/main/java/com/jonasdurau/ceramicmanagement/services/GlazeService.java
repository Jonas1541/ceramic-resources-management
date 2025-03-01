package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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
import com.jonasdurau.ceramicmanagement.dtos.GlazeDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeListDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeMachineUsageDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeResourceUsageDTO;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
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
public class GlazeService {

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

    @Transactional(readOnly = true)
    public List<GlazeListDTO> findAll() {
        List<Glaze> entities = glazeRepository.findAll();
        return entities.stream()
            .map(this::glazeToListDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GlazeDTO findById(Long id) {
        Glaze glaze = glazeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze not found: " + id));
        glaze.getResourceUsages().size();
        glaze.getMachineUsages().size();
        glaze.getTransactions().size();
        return entityToDTO(glaze);
    }

    @Transactional
    public GlazeDTO create(GlazeDTO dto) {
        Glaze glaze = new Glaze();
        copyDTOToEntity(dto, glaze);
        computeUnitCost(glaze);
        glaze = glazeRepository.save(glaze);
        return entityToDTO(glaze);
    }

    @Transactional
    public GlazeDTO update(Long id, GlazeDTO dto) {
        Glaze glaze = glazeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze not found: " + id));
        Map<Long, GlazeResourceUsage> existingResourceUsages = glaze.getResourceUsages().stream()
            .collect(Collectors.toMap(r -> r.getResource().getId(), r -> r));
        for (GlazeResourceUsageDTO usageDTO : dto.getResourceUsages()) {
            GlazeResourceUsage existingUsage = existingResourceUsages.get(usageDTO.getResourceId());
            if (existingUsage != null) {
                existingUsage.setQuantity(usageDTO.getQuantity());
                existingResourceUsages.remove(usageDTO.getResourceId());
            } else {
                Resource resource = resourceRepository.findById(usageDTO.getResourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + usageDTO.getResourceId()));
                GlazeResourceUsage newUsage = new GlazeResourceUsage();
                newUsage.setGlaze(glaze);
                newUsage.setResource(resource);
                newUsage.setQuantity(usageDTO.getQuantity());
                glaze.getResourceUsages().add(newUsage);
            }
        }
        for (GlazeResourceUsage removedUsage : existingResourceUsages.values()) {
            glaze.getResourceUsages().remove(removedUsage);
        }
        Map<Long, GlazeMachineUsage> existingMachineUsages = glaze.getMachineUsages().stream()
            .collect(Collectors.toMap(mu -> mu.getMachine().getId(), mu -> mu));
        Set<Long> updatedMachineIds = new HashSet<>();
        for (GlazeMachineUsageDTO muDTO : dto.getMachineUsages()) {
            GlazeMachineUsage existingMu = existingMachineUsages.get(muDTO.getMachineId());
            if (existingMu != null) {
                existingMu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
                updatedMachineIds.add(muDTO.getMachineId());
            } else {
                Machine machine = machineRepository.findById(muDTO.getMachineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.getMachineId()));
                GlazeMachineUsage newMu = new GlazeMachineUsage();
                newMu.setGlaze(glaze);
                newMu.setMachine(machine);
                newMu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
                glaze.getMachineUsages().add(newMu);
                updatedMachineIds.add(muDTO.getMachineId());
            }
        }
        List<GlazeMachineUsage> muToRemove = glaze.getMachineUsages().stream()
            .filter(mu -> !updatedMachineIds.contains(mu.getMachine().getId()))
            .collect(Collectors.toList());
        for (GlazeMachineUsage mu : muToRemove) {
            glaze.getMachineUsages().remove(mu);
        }
        glaze.setColor(dto.getColor());
        glaze.setUnitValue(dto.getUnitValue());
        computeUnitCost(glaze);
        glaze = glazeRepository.save(glaze);
        return entityToDTO(glaze);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public List<YearReportDTO> getYearlyReport(Long glazeId) {
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

    private GlazeDTO entityToDTO(Glaze entity) {
        GlazeDTO dto = new GlazeDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setColor(entity.getColor());
        dto.setUnitValue(entity.getUnitValue());
        dto.setUnitCost(entity.getUnitCost());
        dto.setCurrentQuantity(entity.getCurrentQuantity());
        dto.setCurrentQuantityPrice(entity.getCurrentQuantityPrice());
        List<GlazeResourceUsageDTO> usageDTOs = entity.getResourceUsages().stream()
            .map(usage -> {
                GlazeResourceUsageDTO u = new GlazeResourceUsageDTO();
                u.setResourceId(usage.getResource().getId());
                u.setQuantity(usage.getQuantity());
                return u;
            })
            .collect(Collectors.toList());
        dto.getResourceUsages().addAll(usageDTOs);
        List<GlazeMachineUsageDTO> machineDTOs = entity.getMachineUsages().stream()
            .map(mu -> {
                GlazeMachineUsageDTO m = new GlazeMachineUsageDTO();
                m.setMachineId(mu.getMachine().getId());
                m.setUsageTimeSeconds(mu.getUsageTime().getSeconds());
                return m;
            })
            .collect(Collectors.toList());
        dto.getMachineUsages().addAll(machineDTOs);
        return dto;
    }

    private void copyDTOToEntity(GlazeDTO dto, Glaze entity) {
        entity.setColor(dto.getColor());
        entity.setUnitValue(dto.getUnitValue());
        for (GlazeResourceUsageDTO usageDTO : dto.getResourceUsages()) {
            Resource resource = resourceRepository.findById(usageDTO.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + usageDTO.getResourceId()));
            GlazeResourceUsage usage = new GlazeResourceUsage();
            usage.setGlaze(entity);
            usage.setResource(resource);
            usage.setQuantity(usageDTO.getQuantity());
            entity.getResourceUsages().add(usage);
        }
        for (GlazeMachineUsageDTO muDTO : dto.getMachineUsages()) {
            Machine machine = machineRepository.findById(muDTO.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.getMachineId()));
            GlazeMachineUsage mu = new GlazeMachineUsage();
            mu.setGlaze(entity);
            mu.setMachine(machine);
            mu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
            entity.getMachineUsages().add(mu);
        }
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

    @Transactional
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

    @Transactional
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

    private GlazeListDTO glazeToListDTO(Glaze entity) {
        GlazeListDTO dto = new GlazeListDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setColor(entity.getColor());
        dto.setUnitValue(entity.getUnitValue());
        dto.setUnitCost(entity.getUnitCost());
        double currentQty = entity.getCurrentQuantity();
        BigDecimal currentQtyPrice = entity.getCurrentQuantityPrice();
        dto.setCurrentQuantity(currentQty);
        dto.setCurrentQuantityPrice(currentQtyPrice);
        return dto;
    }
}
