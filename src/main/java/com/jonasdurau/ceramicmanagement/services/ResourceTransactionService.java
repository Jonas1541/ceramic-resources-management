package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ResourceTransactionDTO;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceTransactionService {

    @Autowired
    private ResourceTransactionRepository transactionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

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
        dto.setResourceId(entity.getResource().getId());
        dto.setCost(entity.getCost().toString());
        return dto;
    }
}
