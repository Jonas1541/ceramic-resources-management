package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ProductTypeDTO;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.repositories.ProductTypeRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;

@Service
public class ProductTypeService implements IndependentCrudService<ProductTypeDTO, ProductTypeDTO, ProductTypeDTO, Long> {
    
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductTypeDTO> findAll() {
        List<ProductType> list = productTypeRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductTypeDTO findById(Long id) {
        ProductType entity = productTypeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public ProductTypeDTO create(ProductTypeDTO dto) {
        if(productTypeRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome " + dto.getName() + " já existe.");
        }
        ProductType entity = new ProductType();
        entity.setName(dto.getName());
        entity = productTypeRepository.save(entity);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public ProductTypeDTO update(Long id, ProductTypeDTO dto) {
        ProductType entity = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado. id: " + id));
        String oldName = entity.getName();
        String newName = dto.getName();
        if(!newName.equals(oldName) && productTypeRepository.existsByName(newName)) {
            throw new BusinessException("O nome " + newName + " já existe.");
        }
        entity.setName(newName);
        entity = productTypeRepository.save(entity);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductType entity = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado. Id " + id));
        boolean hasProducts = productRepository.existsByTypeId(id);
        if(hasProducts) {
            throw new ResourceDeletionException("Não é possível deletar o tipo de produto de Id " + id + " pois ele tem produtos associados.");
        }
        productTypeRepository.delete(entity);
    }

    private ProductTypeDTO entityToDTO(ProductType entity) {
        ProductTypeDTO dto = new ProductTypeDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setProductQuantity(entity.getProductQuantity());
        return dto;
    }
}
