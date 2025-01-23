package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ProductDTO;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductLine;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.repositories.ProductLineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTypeRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository typeRepository;

    @Autowired
    private ProductLineRepository lineRepository;

    @Autowired
    private ProductTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> list = productRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        ProductType type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo não encontrado. Id: " + dto.getTypeId()));
        ProductLine line = lineRepository.findById(dto.getLineId())
                .orElseThrow(() -> new ResourceNotFoundException("Linha não encontrada. Id: " + dto.getLineId()));
        entity.setType(type);
        entity.setLine(line);
        entity = productRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        ProductType type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo não encontrado. Id: " + dto.getTypeId()));
        ProductLine line = lineRepository.findById(dto.getLineId())
                .orElseThrow(() -> new ResourceNotFoundException("Linha não encontrada. Id: " + dto.getLineId()));
        entity.setType(type);
        entity.setLine(line);
        entity = productRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        boolean hasTransactions = transactionRepository.existsByProductId(id);
        if(hasTransactions) {
            throw new ResourceDeletionException("Não é possível deletar o produto com id " + id + " pois ele possui transações associadas.");
        }
        productRepository.delete(entity);
    }

    private ProductDTO entityToDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setHeight(entity.getHeight());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setTypeId(entity.getType().getId());
        dto.setLineId(entity.getLine().getId());
        dto.setProductStock(entity.getProductStock());
        return dto;
    }
}
