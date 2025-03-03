package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.FiringMachineUsageDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeFiringDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlostDTO;
import com.jonasdurau.ceramicmanagement.dtos.KilnDTO;
import com.jonasdurau.ceramicmanagement.entities.FiringMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeFiring;
import com.jonasdurau.ceramicmanagement.entities.GlazeTransaction;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.FiringMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class GlazeFiringService {
    
    @Autowired
    private GlazeFiringRepository firingRepository;

    @Autowired
    private KilnRepository kilnRepository;

    @Autowired
    private ProductTransactionRepository productTransactionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private GlazeTransactionService glazeTransactionService;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private FiringMachineUsageRepository machineUsageRepository;

    @Transactional(readOnly = true)
    public List<GlazeFiringDTO> findAllByKilnId(Long kilnId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        List<GlazeFiring> list = firingRepository.findByKilnId(kilnId);
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public GlazeFiringDTO findById(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        return entityToDTO(entity);
    }

    @Transactional
    public GlazeFiringDTO create(Long kilnId, GlazeFiringDTO dto) {
        GlazeFiring entity = new GlazeFiring();
        entity.setTemperature(dto.getTemperature());
        entity.setBurnTime(dto.getBurnTime());
        entity.setCoolingTime(dto.getCoolingTime());
        entity.setGasConsumption(dto.getGasConsumption());
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId));
        entity.setKiln(kiln);
        entity = firingRepository.save(entity);
        for(GlostDTO glostDTO : dto.getGlosts()) {
            ProductTransaction glost = productTransactionRepository.findById(glostDTO.getProductTransactionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.getProductTransactionId()));
            glost.setGlazeFiring(entity);
            glost.setState(ProductState.GLAZED);
            if (glostDTO.getGlazeId() != null && glostDTO.getQuantity() == null) {
                throw new ResourceNotFoundException("Quantidade de glasura não informada.");
            }
            if (glostDTO.getGlazeId() != null && glostDTO.getQuantity() != null) {
                GlazeTransaction glazeTransaction = glazeTransactionService.createEntity(glostDTO.getGlazeId(), glostDTO.getQuantity());
                glost.setGlazeTransaction(glazeTransaction);
            }
            entity.getGlosts().add(glost);
        }
        if (!dto.getMachineUsages().isEmpty()) {
            for (FiringMachineUsageDTO muDTO : dto.getMachineUsages()) {
                FiringMachineUsage mu = new FiringMachineUsage();
                mu.setUsageTime(muDTO.getUsageTime());
                mu.setGlazeFiring(entity);
                Machine machine = machineRepository.findById(muDTO.getMachineId())
                        .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + muDTO.getMachineId()));
                mu.setMachine(machine);
                mu = machineUsageRepository.save(mu);
                entity.getMachineUsages().add(mu);
            }
        }
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = firingRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public GlazeFiringDTO update(Long kilnId, Long firingId, GlazeFiringDTO dto) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.setTemperature(dto.getTemperature());
        entity.setBurnTime(dto.getBurnTime());
        entity.setCoolingTime(dto.getCoolingTime());
        entity.setGasConsumption(dto.getGasConsumption());
        entity.getGlosts().size();
        List<ProductTransaction> oldList = new ArrayList<>(entity.getGlosts());
        List<ProductTransaction> newList = dto.getGlosts().stream()
                .map(glostDTO -> {
                    ProductTransaction glost = productTransactionRepository.findById(glostDTO.getProductTransactionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.getProductTransactionId()));
                    if (glostDTO.getGlazeId() != null && glostDTO.getQuantity() == null) {
                        throw new ResourceNotFoundException("Quantidade de glasura não informada.");
                    }
                    if (glostDTO.getGlazeId() != null && glostDTO.getQuantity() != null) {
                        GlazeTransaction glazeTransaction = glazeTransactionService.createEntity(glostDTO.getGlazeId(), glostDTO.getQuantity());
                        glost.setGlazeTransaction(glazeTransaction);
                    } else {
                        glost.setGlazeTransaction(null);
                    }
                    glost.setGlazeFiring(entity);
                    glost.setState(ProductState.GLAZED);
                    return glost;
                }).collect(Collectors.toList());
        Set<Long> oldIds = oldList.stream().map(ProductTransaction::getId).collect(Collectors.toSet());
        Set<Long> newIds = newList.stream().map(ProductTransaction::getId).collect(Collectors.toSet());
        List<ProductTransaction> toRemove = oldList.stream().filter(glost -> !newIds.contains(glost.getId())).collect(Collectors.toList());
        toRemove.forEach(glost -> {
            glost.setGlazeFiring(null);
            glost.setState(ProductState.BISCUIT);
            glost.setGlazeTransaction(null);
            productTransactionRepository.save(glost);
        });
        entity.getGlosts().removeAll(toRemove);
        List<ProductTransaction> toAdd = newList.stream().filter(glost -> !oldIds.contains(glost.getId())).collect(Collectors.toList());
        toAdd.forEach(glost -> productTransactionRepository.save(glost));
        entity.getGlosts().addAll(toAdd);
        entity.getMachineUsages().size();
        List<FiringMachineUsage> oldListmu = new ArrayList<>(entity.getMachineUsages());
        List<FiringMachineUsage> newListmu = dto.getMachineUsages().stream()
                .map(muDTO -> {
                    FiringMachineUsage mu = new FiringMachineUsage();
                    mu.setUsageTime(muDTO.getUsageTime());
                    mu.setGlazeFiring(entity);
                    Machine machine = machineRepository.findById(muDTO.getMachineId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Máquina não encontrada. Id: " + muDTO.getMachineId()));
                    mu.setMachine(machine);
                    return mu;
                }).collect(Collectors.toList());
        Set<Long> oldIdsmu = oldListmu.stream().map(FiringMachineUsage::getId).collect(Collectors.toSet());
        Set<Long> newIdsmu = newListmu.stream().map(FiringMachineUsage::getId).collect(Collectors.toSet());
        List<FiringMachineUsage> toRemovemu = oldListmu.stream().filter(mu -> !newIdsmu.contains(mu.getId())).collect(Collectors.toList());
        List<FiringMachineUsage> toAddmu = newListmu.stream().filter(mu -> !oldIdsmu.contains(mu.getId())).collect(Collectors.toList());
        entity.getMachineUsages().removeAll(toRemovemu);
        entity.getMachineUsages().addAll(toAddmu);
        entity.setCostAtTime(calculateCostAtTime(entity));
        GlazeFiring updatedEntity = firingRepository.save(entity);
        return entityToDTO(updatedEntity);
    }
    
    @Transactional
    public void delete(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.getGlosts().size();
        entity.getGlosts().forEach(glost -> {
            glost.setGlazeFiring(null);
            glost.setState(ProductState.BISCUIT);
            glost.setGlazeTransaction(null);
            productTransactionRepository.save(glost);
        });
        firingRepository.delete(entity);
    }

    private GlazeFiringDTO entityToDTO(GlazeFiring entity) {
        GlazeFiringDTO dto = new GlazeFiringDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setTemperature(entity.getTemperature());
        dto.setBurnTime(entity.getBurnTime());
        dto.setCoolingTime(entity.getCoolingTime());
        dto.setCoolingTime(entity.getGasConsumption());
        KilnDTO kilnDTO = new KilnDTO();
        kilnDTO.setId(entity.getKiln().getId());
        kilnDTO.setCreatedAt(entity.getKiln().getCreatedAt());
        kilnDTO.setUpdatedAt(entity.getKiln().getUpdatedAt());
        kilnDTO.setName(entity.getKiln().getName());
        kilnDTO.setPower(entity.getKiln().getPower());
        dto.setKiln(kilnDTO);
        entity.getGlosts().size();
        for(ProductTransaction glost : entity.getGlosts()) {
            GlostDTO glostDTO = new GlostDTO();
            glostDTO.setProductTransactionId(glost.getId());
            if(glost.getGlazeTransaction().getGlaze().getId() != null) {
                glostDTO.setGlazeId(glost.getGlazeTransaction().getGlaze().getId());
                glostDTO.setQuantity(glost.getGlazeTransaction().getQuantity());
            }
            dto.getGlosts().add(glostDTO);
        }
        if (!entity.getMachineUsages().isEmpty()) {
            for (FiringMachineUsage mu : entity.getMachineUsages()) {
                FiringMachineUsageDTO muDTO = new FiringMachineUsageDTO();
                muDTO.setId(mu.getId());
                muDTO.setCreatedAt(mu.getCreatedAt());
                muDTO.setUpdatedAt(mu.getUpdatedAt());
                muDTO.setUsageTime(mu.getUsageTime());
                muDTO.setGlazeFiringId(mu.getGlazeFiring().getId());
                muDTO.setMachineId(mu.getMachine().getId());
                dto.getMachineUsages().add(muDTO);
            }
        }
        dto.setCost(calculateCostAtTime(entity));
        return dto;
    }

    private BigDecimal calculateCostAtTime(GlazeFiring entity) {
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso ELECTRICITY não cadastrada!"));
        Resource gas = resourceRepository.findByCategory(ResourceCategory.GAS)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso GAS não cadastrado!"));
        BigDecimal gasCost = gas.getUnitValue()
                .multiply(BigDecimal.valueOf(entity.getGasConsumption()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal electricCost = electricity.getUnitValue()
                .multiply(BigDecimal.valueOf(entity.getEnergyConsumption()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal costAtTime = gasCost.add(electricCost)
                .setScale(2, RoundingMode.HALF_UP);
        if (!entity.getMachineUsages().isEmpty()) {
            double machineCosts = entity.getMachineUsages().stream()
                    .mapToDouble(FiringMachineUsage::getEnergyConsumption).sum();
            costAtTime = BigDecimal.valueOf(machineCosts).add(costAtTime)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return costAtTime;
    }
}
