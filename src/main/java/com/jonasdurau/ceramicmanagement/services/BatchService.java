package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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
        return batchToDTO(batch);
    }

    @Transactional
    public BatchDTO create(BatchDTO dto) {
        // 1) Cria Batch vazio
        Batch batch = new Batch();

        // 2) Monta resourceUsages
        for (BatchResourceUsageDTO resourceUsageDTO : dto.getResourceUsages()) {
            Resource resource = resourceRepository.findById(resourceUsageDTO.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceUsageDTO.getResourceId()));

            BatchResourceUsage resourceUsage = new BatchResourceUsage();
            resourceUsage.setBatch(batch);
            resourceUsage.setResource(resource);
            resourceUsage.setInitialQuantity(resourceUsageDTO.getInitialQuantity());
            resourceUsage.setUmidity(resourceUsageDTO.getUmidity());
            resourceUsage.setAddedQuantity(resourceUsageDTO.getAddedQuantity());

            // Cria ResourceTransaction (OUTGOING)
            ResourceTransaction tx = new ResourceTransaction();
            tx.setResource(resource);
            tx.setType(TransactionType.OUTGOING);
            tx.setQuantity(resourceUsage.getTotalQuantity());
            resourceTransactionRepository.save(tx);

            batch.getResourceUsages().add(resourceUsage);
        }

        // 3) Monta machineUsages
        for (BatchMachineUsageDTO muDTO : dto.getMachineUsages()) {
            Machine machine = machineRepository.findById(muDTO.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.getMachineId()));

            BatchMachineUsage mu = new BatchMachineUsage();
            mu.setBatch(batch);
            mu.setMachine(machine);
            mu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));

            batch.getMachineUsages().add(mu);
        }

        // 4) Salva batch com as coleções
        batch = batchRepository.save(batch);
        return batchToDTO(batch);
    }

    @Transactional
    public BatchDTO update(Long id, BatchDTO dto) {
        Batch batch = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch not found: " + id));

        // Limpa coleções
        batch.getResourceUsages().clear();
        batch.getMachineUsages().clear();

        // Reconstrói resourceUsages
        for (BatchResourceUsageDTO resourceUsageDTO : dto.getResourceUsages()) {
            Resource resource = resourceRepository.findById(resourceUsageDTO.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + resourceUsageDTO.getResourceId()));

            BatchResourceUsage resourceUsage = new BatchResourceUsage();
            resourceUsage.setBatch(batch);
            resourceUsage.setResource(resource);
            resourceUsage.setInitialQuantity(resourceUsageDTO.getInitialQuantity());
            resourceUsage.setUmidity(resourceUsageDTO.getUmidity());
            resourceUsage.setAddedQuantity(resourceUsageDTO.getAddedQuantity());

            ResourceTransaction tx = new ResourceTransaction();
            tx.setResource(resource);
            tx.setType(TransactionType.OUTGOING);
            tx.setQuantity(resourceUsage.getTotalQuantity());
            resourceTransactionRepository.save(tx);

            batch.getResourceUsages().add(resourceUsage);
        }

        // Reconstrói machineUsages
        for (BatchMachineUsageDTO muDTO : dto.getMachineUsages()) {
            Machine machine = machineRepository.findById(muDTO.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + muDTO.getMachineId()));
            BatchMachineUsage mu = new BatchMachineUsage();
            mu.setBatch(batch);
            mu.setMachine(machine);
            mu.setUsageTime(Duration.ofSeconds(muDTO.getUsageTimeSeconds()));
            batch.getMachineUsages().add(mu);
        }

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
        BigDecimal finalCost = computeBatchFinalCost(entity);
        return new BatchListDTO(entity.getId(), entity.getCreatedAt(), entity.getUpdatedAt(), finalCost);
    }

    private BatchDTO batchToDTO(Batch entity) {
        BatchDTO dto = new BatchDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // ResourceUsage
        List<BatchResourceUsageDTO> usageDTOs = entity.getResourceUsages().stream()
            .map(resourceUsage -> {
                BatchResourceUsageDTO u = new BatchResourceUsageDTO();
                u.setResourceId(resourceUsage.getResource().getId());
                u.setInitialQuantity(resourceUsage.getInitialQuantity());
                u.setUmidity(resourceUsage.getUmidity());
                u.setAddedQuantity(resourceUsage.getAddedQuantity());

                // Campos calculados
                u.setTotalQuantity(resourceUsage.getTotalQuantity());
                u.setTotalWater(resourceUsage.getTotalWater());
                u.setTotalCost(resourceUsage.getTotalCost());

                return u;
            })
            .collect(Collectors.toList());
        dto.setResourceUsages(usageDTOs);

        // MachineUsage
        List<BatchMachineUsageDTO> machineDTOs = entity.getMachineUsages().stream()
            .map(mu -> {
                BatchMachineUsageDTO m = new BatchMachineUsageDTO();
                m.setMachineId(mu.getMachine().getId());
                m.setUsageTimeSeconds(mu.getUsageTime().getSeconds());
                m.setEnergyConsumption(mu.getEnergyConsumption());
                return m;
            })
            .collect(Collectors.toList());
        dto.setMachineUsages(machineDTOs);

        // Campos calculados internos do Batch
        dto.setBatchTotalWater(entity.getBatchTotalWater());
        dto.setResourceTotalQuantity(entity.getResourceTotalQuantity());
        dto.setResourceTotalCost(entity.getResourceTotalCost());
        dto.setMachinesEnergyConsumption(entity.getMachinesEnergyConsumption());

        // Agora calculamos na Service: waterCost, electricityCost, batchFinalCost
        BigDecimal waterCost = computeBatchWaterCost(entity); 
        BigDecimal electricityCost = computeBatchElectricityCost(entity);

        dto.setBatchTotalWaterCost(waterCost);
        // O cost das machines via kWh * resource(ELECTRICITY).unitValue
        dto.setMachinesEnergyConsumptionCost(electricityCost);

        // final = resourceTotalCost (Batch) + waterCost + electricityCost
        BigDecimal finalCost = entity.getResourceTotalCost()
            .add(waterCost)
            .add(electricityCost);
        dto.setBatchFinalCost(finalCost);

        return dto;
    }

    private BigDecimal computeBatchFinalCost(Batch entity) {
        BigDecimal baseResourceCost = entity.getResourceTotalCost();
        BigDecimal waterCost = computeBatchWaterCost(entity);
        BigDecimal electricityCost = computeBatchElectricityCost(entity);
        return baseResourceCost.add(waterCost).add(electricityCost);
    }

    private BigDecimal computeBatchWaterCost(Batch batch) {
        double totalWaterLiters = batch.getBatchTotalWater();
        Resource water = resourceRepository.findByCategory(ResourceCategory.WATER)
            .orElseThrow(() -> new BusinessException("Resource WATER não cadastrada!"));
        return water.getUnitValue().multiply(BigDecimal.valueOf(totalWaterLiters));
    }

    private BigDecimal computeBatchElectricityCost(Batch batch) {
        double totalKwh = batch.getMachinesEnergyConsumption();
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
            .orElseThrow(() -> new BusinessException("Resource ELECTRICITY não cadastrada!"));
        return electricity.getUnitValue().multiply(BigDecimal.valueOf(totalKwh));
    }
}


