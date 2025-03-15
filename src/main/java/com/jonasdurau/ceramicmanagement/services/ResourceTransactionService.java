package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ResourceTransactionDTO;
import com.jonasdurau.ceramicmanagement.entities.Batch;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
import com.jonasdurau.ceramicmanagement.repositories.BatchRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceTransactionService {

    @Autowired
    private ResourceTransactionRepository transactionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Transactional(readOnly = true)
    public List<ResourceTransactionDTO> findAllByResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        List<ResourceTransaction> transactions = transactionRepository.findByResource(resource);
        return transactions.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResourceTransactionDTO findById(Long resourceId, Long transactionId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        return entityToDTO(transaction);
    }

    @Transactional
    public ResourceTransactionDTO create(Long resourceId, ResourceTransactionDTO dto) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = new ResourceTransaction();
        transaction.setType(dto.getType());
        transaction.setQuantity(dto.getQuantity());
        transaction.setResource(resource);
        if (dto.getBatchId() != null) {
            Batch batch = batchRepository.findById(dto.getBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Batch não encontrada. Id: " + dto.getBatchId()));
            transaction.setBatch(batch);
        }
        BigDecimal costAtTime = calculateCostAtTime(resource, dto.getQuantity());
        transaction.setCostAtTime(costAtTime);
        transaction = transactionRepository.save(transaction);
        return entityToDTO(transaction);
    }

    @Transactional
    public ResourceTransactionDTO update(Long resourceId, Long transactionId, ResourceTransactionDTO dto) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        transaction.setType(dto.getType());
        transaction.setQuantity(dto.getQuantity());
        if (dto.getBatchId() != null) {
            Batch batch = batchRepository.findById(dto.getBatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Batch não encontrada. Id: " + dto.getBatchId()));
            transaction.setBatch(batch);
        } else {
            transaction.setBatch(null);
        }
        BigDecimal costAtTime = calculateCostAtTime(resource, dto.getQuantity());
        transaction.setCostAtTime(costAtTime);
        transaction = transactionRepository.save(transaction);
        return entityToDTO(transaction);
    }

    @Transactional
    public void delete(Long resourceId, Long transactionId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        transactionRepository.delete(transaction);
    }

    private ResourceTransactionDTO entityToDTO(ResourceTransaction entity) {
        ResourceTransactionDTO dto = new ResourceTransactionDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setType(entity.getType());
        dto.setQuantity(entity.getQuantity());
        dto.setResourceName(entity.getResource().getName());
        if (entity.getBatch() != null) {
            dto.setBatchId(entity.getBatch().getId());
        } else {
            dto.setBatchId(null);
        }
        dto.setCost(entity.getCostAtTime().setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    private BigDecimal calculateCostAtTime(Resource resource, double quantity) {
        if (resource.getUnitValue() == null) {
            throw new ResourceNotFoundException("Valor unitário do recurso não está definido. Recurso ID: " + resource.getId());
        }
        BigDecimal cost = resource.getUnitValue()
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        return cost;
    }
}
