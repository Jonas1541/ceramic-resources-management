package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ResourceDTO;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<ResourceDTO> findAll() {
        List<Resource> list = resourceRepository.findAll();
        return list.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResourceDTO findById(Long id) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public ResourceDTO create(ResourceDTO dto) {
        if (resourceRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome '" + dto.getName() + "' já existe.");
        }
        Resource entity = dtoToEntity(dto);
        entity = resourceRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public ResourceDTO update(Long id, ResourceDTO dto) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        String newName = dto.getName();
        String oldName = entity.getName();
        if (!oldName.equals(newName) && resourceRepository.existsByName(newName)) {
            throw new BusinessException("O nome '" + newName + "' já existe.");
        }
        entity.setName(newName);
        entity.setCategory(dto.getCategory());
        entity.setUnitValue(dto.getUnitValue());
        entity = resourceRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        boolean hasTransactions = transactionRepository.existsByResourceId(id);
        if (hasTransactions) {
            throw new ResourceDeletionException("Não é possível deletar o recurso com id " + id + " pois ele tem transações associadas.");
        }
        resourceRepository.delete(entity);
    }

    private ResourceDTO entityToDTO(Resource entity) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setUnitValue(entity.getUnitValue());
        return dto;
    }

    private Resource dtoToEntity(ResourceDTO dto) {
        Resource entity = new Resource();
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setUnitValue(dto.getUnitValue());
        return entity;
    }
}
