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
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlostRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlostResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.GlazeFiring;
import com.jonasdurau.ceramicmanagement.entities.GlazeTransaction;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.GlazeFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;
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

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
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
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public GlazeFiringResponseDTO findById(Long kilnId, Long firingId) {
        if(!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public GlazeFiringResponseDTO create(Long kilnId, GlazeFiringRequestDTO dto) {
        GlazeFiring entity = new GlazeFiring();
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId));
        entity.setKiln(kiln);
        entity = firingRepository.save(entity);
        for(GlostRequestDTO glostDTO : dto.glosts()) {
            ProductTransaction glost = productTransactionRepository.findById(glostDTO.productTransactionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.productTransactionId()));
            if(glost.getGlazeFiring() != null  && !glost.getGlazeFiring().getId().equals(entity.getId())) {
                throw new ResourceNotFoundException("Produto já passou por uma 2° queima. Id: " + glost.getId());
            }
            glost.setGlazeFiring(entity);
            glost.setState(ProductState.GLAZED);
            if (glostDTO.glazeId() != null && glostDTO.quantity() == null) {
                throw new ResourceNotFoundException("Quantidade de glasura não informada.");
            }
            if (glostDTO.glazeId() != null && glostDTO.quantity() != null) {
                GlazeTransaction glazeTransaction = glazeTransactionService.createEntity(glostDTO.glazeId(), glostDTO.quantity(), glost);
                glost.setGlazeTransaction(glazeTransaction);
            }
            entity.getGlosts().add(glost);
        }
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = firingRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public GlazeFiringResponseDTO update(Long kilnId, Long firingId, GlazeFiringRequestDTO dto) {
        if (!kilnRepository.existsById(kilnId)) {
            throw new ResourceNotFoundException("Forno não encontrado. Id: " + kilnId);
        }
        GlazeFiring entity = firingRepository.findByIdAndKilnId(firingId, kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Queima não encontrada. Id: " + firingId));
        entity.setTemperature(dto.temperature());
        entity.setBurnTime(dto.burnTime());
        entity.setCoolingTime(dto.coolingTime());
        List<ProductTransaction> oldList = new ArrayList<>(entity.getGlosts());
        List<ProductTransaction> newList = dto.glosts().stream()
                .map(glostDTO -> {
                    ProductTransaction glost = productTransactionRepository.findById(glostDTO.productTransactionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + glostDTO.productTransactionId()));
                    if (glost.getGlazeFiring() != null && !glost.getGlazeFiring().getId().equals(entity.getId())) {
                        throw new ResourceNotFoundException("Produto já passou por uma 2° queima. Id: " + glost.getId());
                    }
                    if (glostDTO.glazeId() != null && glostDTO.quantity() == null) {
                        throw new ResourceNotFoundException("Quantidade de glasura não informada.");
                    }
                    if (glostDTO.glazeId() != null && glostDTO.quantity() != null) {
                        GlazeTransaction glazeTransaction = glazeTransactionService.createEntity(glostDTO.glazeId(), glostDTO.quantity(), glost);
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
        entity.setCostAtTime(calculateCostAtTime(entity));
        GlazeFiring updatedEntity = firingRepository.save(entity);
        return entityToResponseDTO(updatedEntity);
    }
    
    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
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
        for (ProductTransaction glost : entity.getGlosts()) {
            Long productId = glost.getProduct().getId();
            Long productTxId = glost.getId();
            String productName = glost.getProduct().getName();
            String glazeColor;
            Double quantity;
            if (glost.getGlazeTransaction() != null) {
                glazeColor = glost.getGlazeTransaction().getGlaze().getColor();
                quantity = glost.getGlazeTransaction().getQuantity();
            } else {
                glazeColor = "sem glasura";
                quantity = 0.0;
            }
            GlostResponseDTO glostDTO = new GlostResponseDTO(productId, productTxId, productName, glazeColor, quantity);
            glostDTOs.add(glostDTO);
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
        return costAtTime;
    }
}
