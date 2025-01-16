package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.GlazeTransactionDTO;
import com.jonasdurau.ceramicmanagement.entities.Glaze;
import com.jonasdurau.ceramicmanagement.entities.GlazeMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeResourceUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeTransaction;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;
import com.jonasdurau.ceramicmanagement.repositories.GlazeRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class GlazeTransactionService {

    @Autowired
    private GlazeTransactionRepository glazeTransactionRepository;

    @Autowired
    private GlazeRepository glazeRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<GlazeTransactionDTO> findAllByGlaze(Long glazeId) {
        Glaze glaze = glazeRepository.findById(glazeId)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrado. Id: " + glazeId));
        glaze.getTransactions().size();
        return glaze.getTransactions().stream()
            .map(this::entityToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GlazeTransactionDTO findById(Long glazeId, Long transactionId) {
        Glaze glaze = glazeRepository.findById(glazeId)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrado. Id: " + glazeId));
        GlazeTransaction transaction = glaze.getTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        return entityToDTO(transaction);
    }

    public GlazeTransactionDTO create(Long glazeId, GlazeTransactionDTO dto) {
        Glaze glaze = glazeRepository.findById(glazeId)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrado. Id: " + glazeId));
        GlazeTransaction entity = new GlazeTransaction();
        entity.setType(dto.getType());
        entity.setQuantity(dto.getQuantity());
        entity.setGlaze(glaze);
        BigDecimal resourceCost = computeResourceCostAtTime(glaze, dto.getQuantity());
        BigDecimal machineCost = computeMachineCostAtTime(glaze, dto.getQuantity());
        BigDecimal finalCost = resourceCost.add(machineCost).setScale(2, RoundingMode.HALF_UP);
        entity.setResourceTotalCostAtTime(resourceCost);
        entity.setMachineEnergyConsumptionCostAtTime(machineCost);
        entity.setGlazeFinalCostAtTime(finalCost);
        if (dto.getType() == TransactionType.INCOMING) {
            createResourceTransactionsForGlaze(entity, glaze, dto.getQuantity());
        }
        entity = glazeTransactionRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public GlazeTransactionDTO update(Long glazeId, Long transactionId, GlazeTransactionDTO dto) {
        Glaze glaze = glazeRepository.findById(glazeId)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrado. Id: " + glazeId));
        GlazeTransaction transaction = glaze.getTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        transaction.setType(dto.getType());
        transaction.setQuantity(dto.getQuantity());
        BigDecimal resourceCost = computeResourceCostAtTime(glaze, dto.getQuantity());
        BigDecimal machineCost = computeMachineCostAtTime(glaze, dto.getQuantity());
        BigDecimal finalCost = resourceCost.add(machineCost).setScale(2, RoundingMode.HALF_UP);
        transaction.setResourceTotalCostAtTime(resourceCost);
        transaction.setMachineEnergyConsumptionCostAtTime(machineCost);
        transaction.setGlazeFinalCostAtTime(finalCost);
        transaction = glazeTransactionRepository.save(transaction);
        return entityToDTO(transaction);
    }

    @Transactional
    public void delete(Long glazeId, Long transactionId) {
        Glaze glaze = glazeRepository.findById(glazeId)
            .orElseThrow(() -> new ResourceNotFoundException("Glaze não encontrado. Id: " + glazeId));
        GlazeTransaction transaction = glaze.getTransactions().stream()
            .filter(t -> t.getId().equals(transactionId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));

        glaze.getTransactions().remove(transaction);
        glazeTransactionRepository.delete(transaction);
    }

    private void createResourceTransactionsForGlaze(GlazeTransaction glazeTx, Glaze glaze, double quantity) {
        glaze.getResourceUsages().forEach(usage -> {
            double neededQty = usage.getQuantity() * quantity;
            ResourceTransaction rt = new ResourceTransaction();
            rt.setType(TransactionType.OUTGOING);
            rt.setQuantity(neededQty);
            rt.setResource(usage.getResource());
            rt.setGlazeTransaction(glazeTx); // chave principal para cascade
            BigDecimal cost = usage.getResource().getUnitValue()
                    .multiply(BigDecimal.valueOf(neededQty))
                    .setScale(2, RoundingMode.HALF_UP);
            rt.setCostAtTime(cost);
            glazeTx.getResourceTransactions().add(rt);
        });
    }

    private BigDecimal computeResourceCostAtTime(Glaze glaze, double transactionQty) {
        BigDecimal total = BigDecimal.ZERO;
        for (GlazeResourceUsage usage : glaze.getResourceUsages()) {
            double usageFor1kg = usage.getQuantity();  // quanto precisa para 1 kg
            double usageScaled = usageFor1kg * transactionQty; 
            BigDecimal resourceUnitValue = usage.getResource().getUnitValue();
            BigDecimal subCost = resourceUnitValue.multiply(BigDecimal.valueOf(usageScaled));
            total = total.add(subCost);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeMachineCostAtTime(Glaze glaze, double transactionQty) {
        BigDecimal total = BigDecimal.ZERO;
        Resource electricity = resourceRepository.findByCategory(com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory.ELECTRICITY)
            .orElseThrow(() -> new ResourceNotFoundException("Resource ELECTRICITY não cadastrado!"));
        for (GlazeMachineUsage mu : glaze.getMachineUsages()) {
            double baseEnergy = mu.getEnergyConsumption(); // para 1 kg
            double scaledEnergy = baseEnergy * transactionQty;
            BigDecimal electricityRate = electricity.getUnitValue(); 
            BigDecimal subCost = electricityRate.multiply(BigDecimal.valueOf(scaledEnergy));
            total = total.add(subCost);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private GlazeTransactionDTO entityToDTO(GlazeTransaction entity) {
        GlazeTransactionDTO dto = new GlazeTransactionDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setQuantity(entity.getQuantity());
        dto.setType(entity.getType());
        dto.setGlazeId(entity.getGlaze().getId());
        dto.setResourceTotalCostAtTime(entity.getResourceTotalCostAtTime());
        dto.setMachineEnergyConsumptionCostAtTime(entity.getMachineEnergyConsumptionCostAtTime());
        dto.setGlazeFinalCostAtTime(entity.getGlazeFinalCostAtTime());
        return dto;
    }
}

