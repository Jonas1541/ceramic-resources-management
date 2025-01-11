package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.BatchDTO;
import com.jonasdurau.ceramicmanagement.dtos.BatchListDTO;
import com.jonasdurau.ceramicmanagement.dtos.BatchMachineUsageDTO;
import com.jonasdurau.ceramicmanagement.dtos.BatchResourceUsageDTO;
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
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ResourceTransactionRepository resourceTransactionRepository;

    @Transactional(readOnly = true)
    public List<BatchListDTO> findAll() {
        List<Batch> list = batchRepository.findAll();
        return list.stream()
            .map(this::batchToListDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BatchDTO findById(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        batch.getResourceUsages().size();
        batch.getMachineUsages().size();
        batch.getResourceTransactions().size();
        return batchToDTO(batch);
    }

    @Transactional
    public BatchDTO create(BatchDTO dto) {
        Batch batch = new Batch();
        for (BatchResourceUsageDTO resourceUsageDTO : dto.getResourceUsages()) {
            Resource resource = resourceRepository.findById(resourceUsageDTO.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceUsageDTO.getResourceId()));
            BatchResourceUsage resourceUsage = new BatchResourceUsage();
            resourceUsage.setBatch(batch);
            resourceUsage.setResource(resource);
            resourceUsage.setInitialQuantity(resourceUsageDTO.getInitialQuantity());
            resourceUsage.setUmidity(resourceUsageDTO.getUmidity());
            resourceUsage.setAddedQuantity(resourceUsageDTO.getAddedQuantity());
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
        for (BatchMachineUsageDTO muDTO : dto.getMachineUsages()) {
            Machine machine = machineRepository.findById(muDTO.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.getMachineId()));
            BatchMachineUsage mu = new BatchMachineUsage();
            mu.setBatch(batch);
            mu.setMachine(machine);
            mu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
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
        return batchToDTO(batch);
    }

    @Transactional
    public BatchDTO update(Long id, BatchDTO dto) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        Map<Long, BatchResourceUsage> existingResourceUsagesMap = batch.getResourceUsages().stream()
                .collect(Collectors.toMap(bru -> bru.getResource().getId(), bru -> bru));
        for (BatchResourceUsageDTO usageDTO : dto.getResourceUsages()) {
            BatchResourceUsage existingUsage = existingResourceUsagesMap.get(usageDTO.getResourceId());
            if (existingUsage != null) {
                existingUsage.setInitialQuantity(usageDTO.getInitialQuantity());
                existingUsage.setUmidity(usageDTO.getUmidity());
                existingUsage.setAddedQuantity(usageDTO.getAddedQuantity());
                BigDecimal totalCost = existingUsage.getTotalCost();
                existingUsage.setTotalCostAtTime(totalCost);
                ResourceTransaction existingTx = batch.getResourceTransactions().stream()
                        .filter(tx -> tx.getResource().getId().equals(usageDTO.getResourceId())
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
                existingResourceUsagesMap.remove(usageDTO.getResourceId());
            } else {
                Resource resource = resourceRepository.findById(usageDTO.getResourceId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Resource not found: " + usageDTO.getResourceId()));
                BatchResourceUsage newUsage = new BatchResourceUsage();
                newUsage.setBatch(batch);
                newUsage.setResource(resource);
                newUsage.setInitialQuantity(usageDTO.getInitialQuantity());
                newUsage.setUmidity(usageDTO.getUmidity());
                newUsage.setAddedQuantity(usageDTO.getAddedQuantity());
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
        for (BatchMachineUsageDTO muDTO : dto.getMachineUsages()) {
            Long machineId = muDTO.getMachineId();
            BatchMachineUsage existingMu = existingMachineUsagesMap.get(machineId);
            if (existingMu != null) {
                existingMu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
                updatedMachineIds.add(machineId);
            } else {
                Machine machine = machineRepository.findById(machineId)
                        .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

                BatchMachineUsage newMu = new BatchMachineUsage();
                newMu.setBatch(batch);
                newMu.setMachine(machine);
                newMu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
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
        return batchToDTO(batch);
    }

    @Transactional
    public void delete(Long id) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));
        batchRepository.delete(batch);
    }

    private BatchListDTO batchToListDTO(Batch entity) {
        entity.getResourceUsages().size();
        entity.getMachineUsages().size();
        entity.getResourceTransactions().size();

        BigDecimal finalCost = entity.getBatchFinalCostAtTime().setScale(2, RoundingMode.HALF_UP);

        return new BatchListDTO(entity.getId(), entity.getCreatedAt(), entity.getUpdatedAt(), finalCost);
    }

    private BatchDTO batchToDTO(Batch entity) {
        BatchDTO dto = new BatchDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        List<BatchResourceUsageDTO> usageDTOs = entity.getResourceUsages().stream()
            .map(resourceUsage -> {
                BatchResourceUsageDTO u = new BatchResourceUsageDTO();
                u.setResourceId(resourceUsage.getResource().getId());
                u.setInitialQuantity(resourceUsage.getInitialQuantity());
                u.setUmidity(resourceUsage.getUmidity());
                u.setAddedQuantity(resourceUsage.getAddedQuantity());
                u.setTotalQuantity(resourceUsage.getTotalQuantity());
                u.setTotalWater(resourceUsage.getTotalWater());
                u.setTotalCost(resourceUsage.getTotalCostAtTime().setScale(2, RoundingMode.HALF_UP));
                return u;
            })
            .collect(Collectors.toList());
        dto.getResourceUsages().addAll(usageDTOs);
        List<BatchMachineUsageDTO> machineDTOs = entity.getMachineUsages().stream()
            .map(mu -> {
                BatchMachineUsageDTO m = new BatchMachineUsageDTO();
                m.setMachineId(mu.getMachine().getId());
                m.setUsageTimeSeconds(mu.getUsageTime().getSeconds());
                m.setEnergyConsumption(mu.getEnergyConsumption());
                return m;
            })
            .collect(Collectors.toList());
        dto.getMachineUsages().addAll(machineDTOs);
        dto.setBatchTotalWater(entity.getBatchTotalWater());
        dto.setResourceTotalQuantity(entity.getResourceTotalQuantity());
        dto.setResourceTotalCost(entity.getResourceTotalCostAtTime().setScale(2, RoundingMode.HALF_UP));
        dto.setMachinesEnergyConsumption(entity.getMachinesEnergyConsumption());
        dto.setBatchTotalWaterCost(entity.getBatchTotalWaterCostAtTime().setScale(2, RoundingMode.HALF_UP));
        dto.setMachinesEnergyConsumptionCost(entity.getMachinesEnergyConsumptionCostAtTime().setScale(2, RoundingMode.HALF_UP));
        dto.setBatchFinalCost(entity.getBatchFinalCostAtTime().setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    private BigDecimal computeBatchWaterCost(Batch batch) {
        double totalWaterLiters = batch.getBatchTotalWater();
        Resource water = resourceRepository.findByCategory(ResourceCategory.WATER)
            .orElseThrow(() -> new BusinessException("Resource WATER não cadastrada!"));
        return water.getUnitValue()
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
