package com.jonasdurau.ceramicmanagement.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.ProductTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;

@Service
public class ProductTransactionService {
    
    @Autowired
    private ProductTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductTransactionResponseDTO> findAllByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> list = transactionRepository.findByProduct(product);
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductTransactionResponseDTO> findAllByState(ProductState state) {
        List<ProductTransaction> list = transactionRepository.findByState(state);
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductTransactionResponseDTO findById(Long productId, Long transactionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction transaction = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        return entityToDTO(transaction);
    }

    @Transactional
    public List<ProductTransactionResponseDTO> create(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> list = new ArrayList<>();
        for(int i = 0; i < quantity; i++) {
            ProductTransaction entity = new ProductTransaction();
            entity.setState(ProductState.GREENWARE);
            entity.setProduct(product);
            entity = transactionRepository.save(entity);
            list.add(entity);
        }
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional
    public void delete(Long productId, Long transactionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction entity = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + transactionId));
        if(entity.getBisqueFiring() != null && entity.getGlazeFiring() == null) {
            throw new ResourceDeletionException("A transação do produto não pode ser deletada pois está em uma 1° queima.");
        }
        if(entity.getGlazeFiring() != null) {
            throw new ResourceDeletionException("A transação do produto não pode ser deletada pois está em uma 2° queima.");
        }
        transactionRepository.delete(entity);
    }

    @Transactional
    public ProductTransactionResponseDTO outgoing(Long productId, Long transactionId, ProductOutgoingReason outgoingReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction entity = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + transactionId));
        entity.setOutgoingReason(outgoingReason);
        entity.setOutgoingAt(Instant.now());
        entity = transactionRepository.save(entity);
        return entityToDTO(entity);
    }

    private ProductTransactionResponseDTO entityToDTO(ProductTransaction entity) {
        ProductTransactionResponseDTO dto = new ProductTransactionResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setOutgoingAt(entity.getOutgoingAt());
        dto.setState(entity.getState());
        dto.setOutgoingReason(entity.getOutgoingReason());
        dto.setProductName(entity.getProduct().getName());
        if (entity.getGlazeTransaction() != null) {
            dto.setGlazeColor(entity.getGlazeTransaction().getGlaze().getColor());
            dto.setGlazeQuantity(entity.getGlazeTransaction().getQuantity());
        }
        dto.setProfit(entity.getProfit());
        return dto;
    }
}
