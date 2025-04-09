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
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.BatchListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchResourceUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BatchMachineUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BatchResourceUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BatchResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Batch;
import com.jonasdurau.ceramicmanagement.entities.BatchMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.BatchResourceUsage;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;
import com.jonasdurau.ceramicmanagement.repositories.BatchRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

@Service
public class BatchService implements IndependentCrudService<BatchListDTO, BatchRequestDTO, BatchResponseDTO, Long> {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ResourceTransactionRepository resourceTransactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BatchListDTO> findAll() {
        List<Batch> list = batchRepository.findAll();
        return list.stream()
            .map(batch -> new BatchListDTO(
                batch.getId(),
                batch.getCreatedAt(),
                batch.getUpdatedAt(),
                batch.getBatchFinalCostAtTime()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponseDTO findById(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        return batchToResponseDTO(batch);
    }

    @Override
    @Transactional
    public BatchResponseDTO create(BatchRequestDTO dto) {
        Batch batch = new Batch();
        for (BatchResourceUsageRequestDTO resourceUsageDTO : dto.resourceUsages()) {
            Resource resource = resourceRepository.findById(resourceUsageDTO.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceUsageDTO.resourceId()));
            BatchResourceUsage resourceUsage = new BatchResourceUsage();
            resourceUsage.setBatch(batch);
            resourceUsage.setResource(resource);
            resourceUsage.setInitialQuantity(resourceUsageDTO.initialQuantity());
            resourceUsage.setUmidity(resourceUsageDTO.umidity());
            resourceUsage.setAddedQuantity(resourceUsageDTO.addedQuantity());
            BigDecimal totalCost = resourceUsage.getTotalCost();
            resourceUsage.setTotalCostAtTime(totalCost);
            batch.getResourceUsages().add(resourceUsage);
            ResourceTransaction tx = new ResourceTransaction();
            tx.setResource(resource);
            tx.setType(TransactionType.OUTGOING);
            tx.setQuantity(resourceUsage.getTotalQuantity());
            tx.setBatch(batch);
            tx.setCostAtTime(totalCost);
            batch.getResourceTransactions().add(tx);
        }
        for (BatchMachineUsageRequestDTO muDTO : dto.machineUsages()) {
            Machine machine = machineRepository.findById(muDTO.machineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.machineId()));
            BatchMachineUsage mu = new BatchMachineUsage();
            mu.setBatch(batch);
            mu.setMachine(machine);
            mu.setUsageTime(muDTO.usageTime());
            batch.getMachineUsages().add(mu);
        }
        BigDecimal batchTotalWaterCost = computeBatchWaterCost(batch).setScale(2, RoundingMode.HALF_UP);
        BigDecimal resourceTotalCost = batch.getResourceUsages().stream()
            .map(BatchResourceUsage::getTotalCostAtTime)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal machinesEnergyConsumptionCost = computeBatchElectricityCost(batch).setScale(2, RoundingMode.HALF_UP);
        BigDecimal batchFinalCost = resourceTotalCost.add(batchTotalWaterCost).add(machinesEnergyConsumptionCost)
            .setScale(2, RoundingMode.HALF_UP);
        batch.setBatchTotalWaterCostAtTime(batchTotalWaterCost);
        batch.setResourceTotalCostAtTime(resourceTotalCost);
        batch.setMachinesEnergyConsumptionCostAtTime(machinesEnergyConsumptionCost);
        batch.setBatchFinalCostAtTime(batchFinalCost);
        batch = batchRepository.save(batch);
        return batchToResponseDTO(batch);
    }

    @Override
    @Transactional
    public BatchResponseDTO update(Long id, BatchRequestDTO dto) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        Map<Long, BatchResourceUsage> existingResourceUsagesMap = batch.getResourceUsages().stream()
                .collect(Collectors.toMap(bru -> bru.getResource().getId(), bru -> bru));
        for (BatchResourceUsageRequestDTO usageDTO : dto.resourceUsages()) {
            BatchResourceUsage existingUsage = existingResourceUsagesMap.get(usageDTO.resourceId());
            if (existingUsage != null) {
                existingUsage.setInitialQuantity(usageDTO.initialQuantity());
                existingUsage.setUmidity(usageDTO.umidity());
                existingUsage.setAddedQuantity(usageDTO.addedQuantity());
                BigDecimal totalCost = existingUsage.getTotalCost();
                existingUsage.setTotalCostAtTime(totalCost);
                ResourceTransaction existingTx = batch.getResourceTransactions().stream()
                        .filter(tx -> tx.getResource().getId().equals(usageDTO.resourceId())
                                && tx.getType() == TransactionType.OUTGOING)
                        .findFirst()
                        .orElse(null);
                if (existingTx != null) {
                    existingTx.setQuantity(existingUsage.getTotalQuantity());
                    existingTx.setCostAtTime(totalCost);
                } else {
                    ResourceTransaction newTx = new ResourceTransaction();
                    newTx.setResource(existingUsage.getResource());
                    newTx.setType(TransactionType.OUTGOING);
                    newTx.setQuantity(existingUsage.getTotalQuantity());
                    newTx.setBatch(batch);
                    newTx.setCostAtTime(totalCost);
                    batch.getResourceTransactions().add(newTx);
                }
                existingResourceUsagesMap.remove(usageDTO.resourceId());
            } else {
                Resource resource = resourceRepository.findById(usageDTO.resourceId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Resource not found: " + usageDTO.resourceId()));
                BatchResourceUsage newUsage = new BatchResourceUsage();
                newUsage.setBatch(batch);
                newUsage.setResource(resource);
                newUsage.setInitialQuantity(usageDTO.initialQuantity());
                newUsage.setUmidity(usageDTO.umidity());
                newUsage.setAddedQuantity(usageDTO.addedQuantity());
                BigDecimal totalCost = newUsage.getTotalCost();
                newUsage.setTotalCostAtTime(totalCost);
                batch.getResourceUsages().add(newUsage);
                ResourceTransaction newTx = new ResourceTransaction();
                newTx.setResource(resource);
                newTx.setType(TransactionType.OUTGOING);
                newTx.setQuantity(newUsage.getTotalQuantity());
                newTx.setBatch(batch);
                newTx.setCostAtTime(totalCost);
                batch.getResourceTransactions().add(newTx);
            }
        }
        for (BatchResourceUsage remainingUsage : existingResourceUsagesMap.values()) {
            batch.getResourceUsages().remove(remainingUsage);
            ResourceTransaction txToRemove = batch.getResourceTransactions().stream()
                    .filter(tx -> tx.getResource().getId().equals(remainingUsage.getResource().getId())
                            && tx.getType() == TransactionType.OUTGOING)
                    .findFirst()
                    .orElse(null);
            if (txToRemove != null) {
                batch.getResourceTransactions().remove(txToRemove);
                resourceTransactionRepository.delete(txToRemove);
            }
        }
        Map<Long, BatchMachineUsage> existingMachineUsagesMap = batch.getMachineUsages().stream()
                .collect(Collectors.toMap(mu -> mu.getMachine().getId(), mu -> mu));
        Set<Long> updatedMachineIds = new HashSet<>();
        for (BatchMachineUsageRequestDTO muDTO : dto.machineUsages()) {
            Long machineId = muDTO.machineId();
            BatchMachineUsage existingMu = existingMachineUsagesMap.get(machineId);
            if (existingMu != null) {
                existingMu.setUsageTime(muDTO.usageTime());
                updatedMachineIds.add(machineId);
            } else {
                Machine machine = machineRepository.findById(machineId)
                        .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

                BatchMachineUsage newMu = new BatchMachineUsage();
                newMu.setBatch(batch);
                newMu.setMachine(machine);
                newMu.setUsageTime(muDTO.usageTime());
                batch.getMachineUsages().add(newMu);
                updatedMachineIds.add(machineId);
            }
        }
        List<BatchMachineUsage> toRemove = batch.getMachineUsages().stream()
                .filter(mu -> !updatedMachineIds.contains(mu.getMachine().getId()))
                .collect(Collectors.toList());
        for (BatchMachineUsage mu : toRemove) {
            batch.getMachineUsages().remove(mu);
        }
        BigDecimal batchTotalWaterCost = computeBatchWaterCost(batch).setScale(2, RoundingMode.HALF_UP);
        BigDecimal resourceTotalCost = batch.getResourceUsages().stream()
                .map(BatchResourceUsage::getTotalCostAtTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal machinesEnergyConsumptionCost = computeBatchElectricityCost(batch).setScale(2, RoundingMode.HALF_UP);
        BigDecimal batchFinalCost = resourceTotalCost.add(batchTotalWaterCost).add(machinesEnergyConsumptionCost)
                .setScale(2, RoundingMode.HALF_UP);
        batch.setBatchTotalWaterCostAtTime(batchTotalWaterCost);
        batch.setResourceTotalCostAtTime(resourceTotalCost);
        batch.setMachinesEnergyConsumptionCostAtTime(machinesEnergyConsumptionCost);
        batch.setBatchFinalCostAtTime(batchFinalCost);
        batch = batchRepository.save(batch);
        return batchToResponseDTO(batch);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        batchRepository.delete(batch);
    }

    @Transactional(readOnly = true)
    public List<YearReportDTO> yearlyReport() {
        List<Batch> batches = batchRepository.findAll();
        ZoneId zone = ZoneId.systemDefault();
        Map<Integer, Map<Month, List<Batch>>> mapYearMonth = batches.stream()
                .map(b -> new AbstractMap.SimpleEntry<>(b, b.getCreatedAt().atZone(zone)))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getValue().getMonth(),
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<Batch>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<Batch>> mapMonth = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            double totalOutgoingQtyYear = 0.0;
            BigDecimal totalOutgoingProfitYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<Batch> monthBatches = mapMonth.getOrDefault(m, Collections.emptyList());
                double incomingQty = 0.0;
                BigDecimal incomingCost = BigDecimal.ZERO;
                double outgoingQty = 0.0;
                BigDecimal outgoingProfit = BigDecimal.ZERO;
                for (Batch batch : monthBatches) {
                    incomingQty += batch.getResourceTotalQuantity();
                    incomingCost = incomingCost
                            .add(batch.getBatchFinalCostAtTime() != null ? batch.getBatchFinalCostAtTime()
                                    : BigDecimal.ZERO);
                }
                totalIncomingQtyYear += incomingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
                totalOutgoingQtyYear += outgoingQty;
                totalOutgoingProfitYear = totalOutgoingProfitYear.add(outgoingProfit);
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

    private BatchResponseDTO batchToResponseDTO(Batch entity) {
        List<BatchResourceUsageResponseDTO> resourceUsageDTOs = entity.getResourceUsages().stream()
        .map(resourceUsage -> new BatchResourceUsageResponseDTO(
            resourceUsage.getResource().getId(),
            resourceUsage.getResource().getName(),
            resourceUsage.getInitialQuantity(),
            resourceUsage.getUmidity(),
            resourceUsage.getAddedQuantity(),
            resourceUsage.getTotalQuantity(),
            resourceUsage.getTotalWater(),
            resourceUsage.getTotalCostAtTime().setScale(2, RoundingMode.HALF_UP)
        ))
        .collect(Collectors.toList());
        List<BatchMachineUsageResponseDTO> machineUsageDTOs = entity.getMachineUsages().stream()
        .map(mu -> new BatchMachineUsageResponseDTO(
            mu.getMachine().getId(),
            mu.getMachine().getName(),
            mu.getUsageTime(),
            mu.getEnergyConsumption()
        ))
        .collect(Collectors.toList());
        BatchResponseDTO dto = new BatchResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            resourceUsageDTOs,
            machineUsageDTOs,
            entity.getBatchTotalWater(),
            entity.getBatchTotalWaterCostAtTime().setScale(2, RoundingMode.HALF_UP),
            entity.getResourceTotalQuantity(),
            entity.getResourceTotalCost().setScale(2, RoundingMode.HALF_UP),
            entity.getMachinesEnergyConsumption(),
            entity.getMachinesEnergyConsumptionCostAtTime().setScale(2, RoundingMode.HALF_UP),
            entity.getBatchFinalCostAtTime().setScale(2, RoundingMode.HALF_UP)
        );
        return dto;
    }

    private BigDecimal computeBatchWaterCost(Batch batch) {
        double totalWaterLiters = batch.getBatchTotalWater();
        Resource water = resourceRepository.findByCategory(ResourceCategory.WATER)
            .orElseThrow(() -> new BusinessException("Resource WATER não cadastrada!"));
        return water.getUnitValue()
            .divide(BigDecimal.valueOf(1000), 10, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(totalWaterLiters))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeBatchElectricityCost(Batch batch) {
        double totalKwh = batch.getMachinesEnergyConsumption();
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
            .orElseThrow(() -> new BusinessException("Resource ELECTRICITY não cadastrada!"));
        return electricity.getUnitValue()
            .multiply(BigDecimal.valueOf(totalKwh))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
