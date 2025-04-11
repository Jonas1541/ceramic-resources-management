package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.request.ResourceTransactionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ResourceTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
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
public class ResourceTransactionService implements DependentCrudService<ResourceTransactionResponseDTO, ResourceTransactionRequestDTO,  ResourceTransactionResponseDTO, Long> {

    @Autowired
    private ResourceTransactionRepository transactionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResourceTransactionResponseDTO> findAllByParentId(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        List<ResourceTransaction> transactions = transactionRepository.findByResource(resource);
        return transactions.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceTransactionResponseDTO findById(Long resourceId, Long transactionId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        return entityToDTO(transaction);
    }

    @Override
    @Transactional
    public ResourceTransactionResponseDTO create(Long resourceId, ResourceTransactionRequestDTO dto) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = new ResourceTransaction();
        transaction.setType(dto.type());
        transaction.setQuantity(dto.quantity());
        transaction.setResource(resource);
        BigDecimal costAtTime = calculateCostAtTime(resource, dto.quantity());
        transaction.setCostAtTime(costAtTime);
        transaction = transactionRepository.save(transaction);
        return entityToDTO(transaction);
    }

    @Override
    @Transactional
    public ResourceTransactionResponseDTO update(Long resourceId, Long transactionId, ResourceTransactionRequestDTO dto) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        transaction.setType(dto.type());
        transaction.setQuantity(dto.quantity());
        BigDecimal costAtTime = calculateCostAtTime(resource, dto.quantity());
        transaction.setCostAtTime(costAtTime);
        transaction = transactionRepository.save(transaction);
        return entityToDTO(transaction);
    }

    @Override
    @Transactional
    public void delete(Long resourceId, Long transactionId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + resourceId));
        ResourceTransaction transaction = transactionRepository.findByIdAndResource(transactionId, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        transactionRepository.delete(transaction);
    }

    private ResourceTransactionResponseDTO entityToDTO(ResourceTransaction entity) {
        Long batchId = entity.getBatch() != null ? entity.getBatch().getId() : null;
        return new ResourceTransactionResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getType(),
            entity.getQuantity(),
            entity.getResource().getName(),
            batchId,
            entity.getCostAtTime().setScale(2, RoundingMode.HALF_UP)
        );
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
