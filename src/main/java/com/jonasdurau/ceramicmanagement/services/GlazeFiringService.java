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
import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.FiringMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlostRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.FiringMachineUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlostResponseDTO;
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
public class GlazeFiringService implements DependentCrudService<FiringListDTO, GlazeFiringRequestDTO, GlazeFiringResponseDTO, Long> {
    
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

    @Override
    @Transactional(readOnly = true)
    public List<FiringListDTO> findAllByParentId(Long kilnId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        List<GlazeFiring> list = firingRepository.findByKilnId(kilnId);
        return list.stream()
            .map(firing -> new FiringListDTO(
                firing.getId(),
                firing.getCreatedAt(),
                firing.getUpdatedAt(),
                firing.getTemperature(),
                firing.getBurnTime(),
                firing.getCoolingTime(),
                firing.getGasConsumption(),
                firing.getKiln().getName(),
                firing.getCostAtTime()
            )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GlazeFiringResponseDTO findById(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional
    public GlazeFiringResponseDTO create(Long kilnId, GlazeFiringRequestDTO dto) {
        GlazeFiring entity = new GlazeFiring();
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        entity.setGasConsumption(dto.gasConsumption());
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId));
        entity.setKiln(kiln);
        entity = firingRepository.save(entity);
        for(GlostRequestDTO glostDTO : dto.glosts()) {
            ProductTransaction glost = productTransactionRepository.findById(glostDTO.getProductTransactionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.getProductTransactionId()));
            if(glost.getGlazeFiring() != null  && !glost.getGlazeFiring().getId().equals(entity.getId())) {
                throw new ResourceNotFoundException("Produto já passou por uma 2° queima. Id: " + glost.getId());
            }
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
        if (!dto.machineUsages().isEmpty()) {
            for (FiringMachineUsageRequestDTO muDTO : dto.machineUsages()) {
                FiringMachineUsage mu = new FiringMachineUsage();
                mu.setUsageTime(muDTO.usageTime());
                mu.setGlazeFiring(entity);
                Machine machine = machineRepository.findById(muDTO.machineId())
                        .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + muDTO.machineId()));
                mu.setMachine(machine);
                mu = machineUsageRepository.save(mu);
                entity.getMachineUsages().add(mu);
            }
        }
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = firingRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional
    public GlazeFiringResponseDTO update(Long kilnId, Long firingId, GlazeFiringRequestDTO dto) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        entity.setGasConsumption(dto.gasConsumption());
        List<ProductTransaction> oldList = new ArrayList<>(entity.getGlosts());
        List<ProductTransaction> newList = dto.glosts().stream()
                .map(glostDTO -> {
                    ProductTransaction glost = productTransactionRepository.findById(glostDTO.getProductTransactionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.getProductTransactionId()));
                    if (glost.getGlazeFiring() != null && !glost.getGlazeFiring().getId().equals(entity.getId())) {
                        throw new ResourceNotFoundException("Produto já passou por uma 2° queima. Id: " + glost.getId());
                    }
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
        List<FiringMachineUsage> oldListmu = new ArrayList<>(entity.getMachineUsages());
        List<FiringMachineUsage> newListmu = dto.machineUsages().stream()
                .map(muDTO -> {
                    FiringMachineUsage mu = new FiringMachineUsage();
                    mu.setUsageTime(muDTO.usageTime());
                    mu.setGlazeFiring(entity);
                    Machine machine = machineRepository.findById(muDTO.machineId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Máquina não encontrada. Id: " + muDTO.machineId()));
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
        return entityToResponseDTO(updatedEntity);
    }
    
    @Override
    @Transactional
    public void delete(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.getGlosts().forEach(glost -> {
            glost.setGlazeFiring(null);
            glost.setState(ProductState.BISCUIT);
            glost.setGlazeTransaction(null);
            productTransactionRepository.save(glost);
        });
        firingRepository.delete(entity);
    }

    private GlazeFiringResponseDTO entityToResponseDTO(GlazeFiring entity) {
        List<GlostResponseDTO> glostDTOs = new ArrayList<>();
        for(ProductTransaction glost : entity.getGlosts()) {
            GlostResponseDTO glostDTO = new GlostResponseDTO();
            glostDTO.setProductName(glost.getProduct().getName());
            if(glost.getGlazeTransaction() != null) {
                glostDTO.setGlazeColor(glost.getGlazeTransaction().getGlaze().getColor());
                glostDTO.setQuantity(glost.getGlazeTransaction().getQuantity());
            }
            else {
                glostDTO.setGlazeColor("sem glasura");
                glostDTO.setQuantity(0.0);
            }
            glostDTOs.add(glostDTO);
        }
        List<FiringMachineUsageResponseDTO> machineUsageDTOs = new ArrayList<>();
        if (!entity.getMachineUsages().isEmpty()) {
            for (FiringMachineUsage mu : entity.getMachineUsages()) {
                FiringMachineUsageResponseDTO muDTO = new FiringMachineUsageResponseDTO(
                    mu.getId(),
                    mu.getCreatedAt(),
                    mu.getUpdatedAt(),
                    mu.getUsageTime(),
                    mu.getMachine().getName()
                );
                machineUsageDTOs.add(muDTO);
            }
        }
        return new GlazeFiringResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getTemperature(),
            entity.getBurnTime(),
            entity.getCoolingTime(),
            entity.getGasConsumption(),
            entity.getKiln().getName(),
            glostDTOs,
            machineUsageDTOs,
            calculateCostAtTime(entity)
        );
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
