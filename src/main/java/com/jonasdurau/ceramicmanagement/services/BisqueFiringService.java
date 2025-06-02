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

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BisqueFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.FiringMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BisqueFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.FiringMachineUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.BisqueFiring;
import com.jonasdurau.ceramicmanagement.entities.FiringMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.BisqueFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.FiringMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class BisqueFiringService implements DependentCrudService<FiringListDTO, BisqueFiringRequestDTO, BisqueFiringResponseDTO, Long> {
    
    @Autowired
    private BisqueFiringRepository firingRepository;

    @Autowired
    private KilnRepository kilnRepository;

    @Autowired
    private ProductTransactionRepository productTransactionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private FiringMachineUsageRepository machineUsageRepository;

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<FiringListDTO> findAllByParentId(Long kilnId) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        List<BisqueFiring> list = firingRepository.findByKilnId(kilnId);
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
            ))
            .toList();
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public BisqueFiringResponseDTO findById(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public BisqueFiringResponseDTO create(Long kilnId, BisqueFiringRequestDTO dto) {
        BisqueFiring entity = new BisqueFiring();
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        entity.setGasConsumption(dto.gasConsumption());
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId));
        entity.setKiln(kiln);
        entity = firingRepository.save(entity);
        for(long biscuitId : dto.biscuits()) {
            ProductTransaction biscuit = productTransactionRepository.findById(biscuitId)
                    .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + biscuitId));
            if(biscuit.getBisqueFiring() != null  && !biscuit.getBisqueFiring().getId().equals(entity.getId())) {
                throw new BusinessException("Produto já passou por uma 1° queima. Id: " + biscuitId);
            }
            biscuit.setBisqueFiring(entity);
            biscuit.setState(ProductState.BISCUIT);
            entity.getBiscuits().add(biscuit);
        }
        if(!dto.machineUsages().isEmpty()) {
            for(FiringMachineUsageRequestDTO muDTO : dto.machineUsages()) {
                FiringMachineUsage mu = new FiringMachineUsage();
                mu.setUsageTime(muDTO.usageTime());
                mu.setBisqueFiring(entity);
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
    @Transactional(transactionManager = "tenantTransactionManager")
    public BisqueFiringResponseDTO update(Long kilnId, Long firingId, BisqueFiringRequestDTO dto) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        entity.setGasConsumption(dto.gasConsumption());
        List<ProductTransaction> oldList = new ArrayList<>(entity.getBiscuits());
        List<ProductTransaction> newList = dto.biscuits().stream()
                .map(id -> {
                    ProductTransaction biscuit = productTransactionRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + id));
                    return biscuit;
                }).collect(Collectors.toList());
        Set<Long> oldIds = oldList.stream().map(ProductTransaction::getId).collect(Collectors.toSet());
        Set<Long> newIds = newList.stream().map(ProductTransaction::getId).collect(Collectors.toSet());
        List<ProductTransaction> toRemove = oldList.stream().filter(biscuit -> !newIds.contains(biscuit.getId())).collect(Collectors.toList());
        List<ProductTransaction> toAdd = newList.stream().filter(biscuit -> !oldIds.contains(biscuit.getId())).collect(Collectors.toList());
        toRemove.forEach(biscuit -> {
            if(biscuit.getState() == ProductState.GLAZED) {
                throw new ResourceDeletionException("A queima não pode ser apagada pois há um produto que já passou pela 2° queima. Id: " + biscuit.getId());
            }
            biscuit.setBisqueFiring(null);
            biscuit.setState(ProductState.GREENWARE);
            productTransactionRepository.save(biscuit);
        });
        entity.getBiscuits().removeAll(toRemove);
        toAdd.forEach(biscuit -> {
            if(biscuit.getBisqueFiring() != null && !biscuit.getBisqueFiring().getId().equals(entity.getId())) {
                throw new BusinessException("Produto já passou por uma 1° queima. Id: " + biscuit.getId());
            }
            biscuit.setBisqueFiring(entity);
            biscuit.setState(ProductState.BISCUIT);
            productTransactionRepository.save(biscuit);
        });
        entity.getBiscuits().addAll(toAdd);
        List<FiringMachineUsage> oldListmu = new ArrayList<>(entity.getMachineUsages());
        List<FiringMachineUsage> newListmu = dto.machineUsages().stream()
                .map(muDTO -> {
                    FiringMachineUsage mu = new FiringMachineUsage();
                    mu.setUsageTime(muDTO.usageTime());
                    mu.setBisqueFiring(entity);
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
        BisqueFiring updatedEntity = firingRepository.save(entity);
        return entityToResponseDTO(updatedEntity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public void delete(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.getBiscuits().forEach(biscuit -> {
            if (biscuit.getState() == ProductState.GLAZED) {
                throw new ResourceDeletionException("A queima não pode ser apagada pois há um produto que já passou pela 2° queima. Id: "+ biscuit.getId());
            }
            biscuit.setBisqueFiring(null);
            biscuit.setState(ProductState.GREENWARE);
            productTransactionRepository.save(biscuit);
        });
        firingRepository.delete(entity);
    }

    private BisqueFiringResponseDTO entityToResponseDTO(BisqueFiring entity) {
        List<ProductTransactionResponseDTO> biscuitDTOs = new ArrayList<>();
        for(ProductTransaction biscuit : entity.getBiscuits()) {
            String productName = biscuit.getProduct().getName();
            String glazeColor = "sem glasura";
            double glazeQuantity = 0;
            if (biscuit.getGlazeTransaction() != null && biscuit.getGlazeTransaction().getGlaze() != null) {
                glazeColor = biscuit.getGlazeTransaction().getGlaze().getColor();
                glazeQuantity = biscuit.getGlazeTransaction().getQuantity();
            }
            ProductTransactionResponseDTO biscuitDTO = new ProductTransactionResponseDTO(
                biscuit.getId(),
                biscuit.getCreatedAt(),
                biscuit.getUpdatedAt(),
                biscuit.getOutgoingAt(),
                biscuit.getState(),
                biscuit.getOutgoingReason(),
                productName,
                glazeColor,
                glazeQuantity,
                biscuit.getProfit()
            );
            biscuitDTOs.add(biscuitDTO);
        }
        List<FiringMachineUsageResponseDTO> machineUsageDTOs = new ArrayList<>();
        if(!entity.getMachineUsages().isEmpty()) {
            for(FiringMachineUsage mu : entity.getMachineUsages()) {
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
        BisqueFiringResponseDTO dto = new BisqueFiringResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getTemperature(),
            entity.getBurnTime(),
            entity.getCoolingTime(),
            entity.getGasConsumption(),
            entity.getKiln().getName(),
            biscuitDTOs,
            machineUsageDTOs,
            calculateCostAtTime(entity)
        );
        return dto;
    }

    private BigDecimal calculateCostAtTime(BisqueFiring entity) {
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso ELECTRICITY não cadastrada!"));
        Resource gas = resourceRepository.findByCategory(ResourceCategory.GAS)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso GAS não cadastrada!"));
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
