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

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.BisqueFiringDTO;
import com.jonasdurau.ceramicmanagement.dtos.FiringMachineUsageDTO;
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
public class BisqueFiringService {
    
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

    @Transactional(readOnly = true)
    public List<BisqueFiringDTO> findAllByKilnId(Long kilnId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        List<BisqueFiring> list = firingRepository.findByKilnId(kilnId);
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public BisqueFiringDTO findById(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        return entityToDTO(entity);
    }

    @Transactional
    public BisqueFiringDTO create(Long kilnId, BisqueFiringDTO dto) {
        BisqueFiring entity = new BisqueFiring();
        entity.setTemperature(dto.getTemperature());
        entity.setBurnTime(dto.getBurnTime());
        entity.setCoolingTime(dto.getCoolingTime());
        entity.setGasConsumption(dto.getGasConsumption());
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + dto.getKilnId()));
        entity.setKiln(kiln);
        entity = firingRepository.save(entity);
        for(long biscuitId : dto.getBiscuits()) {
            ProductTransaction biscuit = productTransactionRepository.findById(biscuitId)
                    .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + biscuitId));
            biscuit.setBisqueFiring(entity);
            biscuit.setState(ProductState.BISCUIT);
            entity.getBiscuits().add(biscuit);
        }
        if(!dto.getMachineUsages().isEmpty()) {
            for(FiringMachineUsageDTO muDTO : dto.getMachineUsages()) {
                FiringMachineUsage mu = new FiringMachineUsage();
                mu.setUsageTime(muDTO.getUsageTime());
                mu.setBisqueFiring(entity);
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
    public BisqueFiringDTO update(Long kilnId, Long firingId, BisqueFiringDTO dto) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.setTemperature(dto.getTemperature());
        entity.setBurnTime(dto.getBurnTime());
        entity.setCoolingTime(dto.getCoolingTime());
        entity.setGasConsumption(dto.getGasConsumption());
        entity.getBiscuits().size();
        List<ProductTransaction> oldList = new ArrayList<>(entity.getBiscuits());
        List<ProductTransaction> newList = dto.getBiscuits().stream()
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
            biscuit.setBisqueFiring(entity);
            biscuit.setState(ProductState.BISCUIT);
            productTransactionRepository.save(biscuit);
        });
        entity.getBiscuits().addAll(toAdd);
        entity.getMachineUsages().size();
        List<FiringMachineUsage> oldListmu = new ArrayList<>(entity.getMachineUsages());
        List<FiringMachineUsage> newListmu = dto.getMachineUsages().stream()
                .map(muDTO -> {
                    FiringMachineUsage mu = new FiringMachineUsage();
                    mu.setUsageTime(muDTO.getUsageTime());
                    mu.setBisqueFiring(entity);
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
        BisqueFiring updatedEntity = firingRepository.save(entity);
        return entityToDTO(updatedEntity);
    }

    @Transactional
    public void delete(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. id: " + kilnId);
        }
        BisqueFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
                entity.getBiscuits().size();
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

    private BisqueFiringDTO entityToDTO(BisqueFiring entity) {
        BisqueFiringDTO dto = new BisqueFiringDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setTemperature(entity.getTemperature());
        dto.setBurnTime(entity.getBurnTime());
        dto.setCoolingTime(entity.getCoolingTime());
        dto.setGasConsumption(entity.getGasConsumption());
        dto.setKilnId(entity.getKiln().getId());
        entity.getBiscuits().size();
        for(ProductTransaction biscuit : entity.getBiscuits()) {
            dto.getBiscuits().add(biscuit.getId());
        }
        if(!entity.getMachineUsages().isEmpty()) {
            for(FiringMachineUsage mu : entity.getMachineUsages()) {
                FiringMachineUsageDTO muDTO = new FiringMachineUsageDTO();
                muDTO.setId(mu.getId());
                muDTO.setCreatedAt(mu.getCreatedAt());
                muDTO.setUpdatedAt(mu.getUpdatedAt());
                muDTO.setUsageTime(mu.getUsageTime());
                muDTO.setBisqueFiringId(mu.getBisqueFiring().getId());
                muDTO.setMachineId(mu.getMachine().getId());
                dto.getMachineUsages().add(muDTO);
            }
        }
        dto.setCost(calculateCostAtTime(entity));
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
