package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.request.ProductTypeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTypeResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.repositories.ProductTypeRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;

@Service
public class ProductTypeService implements IndependentCrudService<ProductTypeResponseDTO, ProductTypeRequestDTO, ProductTypeResponseDTO, Long> {
    
    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductTypeResponseDTO> findAll() {
        List<ProductType> list = productTypeRepository.findAll();
        return list.stream().map(this::entityToResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductTypeResponseDTO findById(Long id) {
        ProductType entity = productTypeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado. Id: " + id));
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional
    public ProductTypeResponseDTO create(ProductTypeRequestDTO dto) {
        if(productTypeRepository.existsByName(dto.name())) {
            throw new BusinessException("O nome " + dto.name() + " já existe.");
        }
        ProductType entity = new ProductType();
        entity.setName(dto.name());
        entity = productTypeRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional
    public ProductTypeResponseDTO update(Long id, ProductTypeRequestDTO dto) {
        ProductType entity = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de produto não encontrado. id: " + id));
        String oldName = entity.getName();
        String newName = dto.name();
        if(!newName.equals(oldName) && productTypeRepository.existsByName(newName)) {
            throw new BusinessException("O nome " + newName + " já existe.");
        }
        entity.setName(newName);
        entity = productTypeRepository.save(entity);
        return entityToResponseDTO(entity);
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

    private ProductTypeResponseDTO entityToResponseDTO(ProductType entity) {
        ProductTypeResponseDTO dto = new ProductTypeResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getName(),
            entity.getProductQuantity()
        );
        return dto;
    }
}
