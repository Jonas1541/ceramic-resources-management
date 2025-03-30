package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ProductLineDTO;
import com.jonasdurau.ceramicmanagement.entities.ProductLine;
import com.jonasdurau.ceramicmanagement.repositories.ProductLineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;

@Service
public class ProductLineService implements IndependentCrudService<ProductLineDTO, ProductLineDTO, ProductLineDTO, Long> {
    
    @Autowired
    private ProductLineRepository productLineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductLineDTO> findAll() {
        List<ProductLine> list = productLineRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductLineDTO findById(Long id) {
        ProductLine entity = productLineRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Linha de produto não encontrada. Id: " + id));
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public ProductLineDTO create(ProductLineDTO dto) {
        if(productLineRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome " + dto.getName() + " já existe.");
        }
        ProductLine entity = new ProductLine();
        entity.setName(dto.getName());
        entity = productLineRepository.save(entity);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public ProductLineDTO update(Long id, ProductLineDTO dto) {
        ProductLine entity = productLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de produto não encontrada. id: " + id));
        String oldName = entity.getName();
        String newName = dto.getName();
        if(!newName.equals(oldName) && productLineRepository.existsByName(newName)) {
            throw new BusinessException("O nome " + newName + " já existe.");
        }
        entity.setName(newName);
        entity = productLineRepository.save(entity);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductLine entity = productLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha de produto não encontrada. Id " + id));
        boolean hasProducts = productRepository.existsByLineId(id);
        if(hasProducts) {
            throw new ResourceDeletionException("Não é possível deletar a linha de produtos de Id " + id + " pois ela tem produtos associados.");
        }
        productLineRepository.delete(entity);
    }

    private ProductLineDTO entityToDTO(ProductLine entity) {
        ProductLineDTO dto = new ProductLineDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setProductQuantity(entity.getProductQuantity());
        return dto;
    }
}
