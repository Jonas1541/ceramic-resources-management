package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.repositories.BatchRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;

@Service
public class ProductTransactionService {
    
    @Autowired
    private ProductTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<ProductTransactionResponseDTO> findAllByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> list = transactionRepository.findByProduct(product);
        return list.stream().map(this::entityToResponseDTO).toList();
    }

    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<ProductTransactionResponseDTO> findAllByState(ProductState state) {
        List<ProductTransaction> list = transactionRepository.findByState(state);
        return list.stream().map(this::entityToResponseDTO).toList();
    }

    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public ProductTransactionResponseDTO findById(Long productId, Long transactionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction transaction = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada. Id: " + transactionId));
        return entityToResponseDTO(transaction);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public List<ProductTransactionResponseDTO> create(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> list = new ArrayList<>();
        for(int i = 0; i < quantity; i++) {
            ProductTransaction entity = new ProductTransaction();
            entity.setState(ProductState.GREENWARE);
            entity.setProduct(product);
            entity.setCost(calculateProductTransactionCost(product.getWeight()));
            entity = transactionRepository.save(entity);
            list.add(entity);
        }
        return list.stream().map(this::entityToResponseDTO).toList();
    }

    @Transactional(transactionManager = "tenantTransactionManager")
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

    @Transactional(transactionManager = "tenantTransactionManager")
    public ProductTransactionResponseDTO outgoing(Long productId, Long transactionId, ProductOutgoingReason outgoingReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction entity = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + transactionId));
        entity.setOutgoingReason(outgoingReason);
        entity.setOutgoingAt(Instant.now());
        entity = transactionRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public List<ProductTransactionResponseDTO> outgoingByQuantity(Long productId, int quantity, ProductState state, ProductOutgoingReason outgoingReason) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> availableTransactions = transactionRepository
                .findByProductAndStateAndOutgoingReasonIsNullOrderByCreatedAtAsc(product, state, PageRequest.of(0, quantity));
        if (availableTransactions.size() < quantity) {
            throw new ResourceNotFoundException("Quantidade solicitada maior que o estoque disponível.");
        }
        Instant now = Instant.now();
        for (ProductTransaction tx : availableTransactions) {
            tx.setOutgoingReason(outgoingReason);
            tx.setOutgoingAt(now);
        }
        List<ProductTransaction> saved = transactionRepository.saveAll(availableTransactions);
        return saved.stream().map(this::entityToResponseDTO).toList();
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public ProductTransactionResponseDTO cancelOutgoing(Long productId, Long transactionId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        ProductTransaction entity = transactionRepository.findByIdAndProduct(transactionId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Transação de produto não encontrada. Id: " + transactionId));
        entity.setOutgoingReason(null);
        entity.setOutgoingAt(null);
        entity = transactionRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Transactional(transactionManager = "tenantTransactionManager")
    public List<ProductTransactionResponseDTO> cancelOutgoingByQuantity(Long productId, int quantity, ProductState state) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + productId));
        List<ProductTransaction> availableTransactions = transactionRepository
                .findByProductAndStateAndOutgoingReasonIsNotNullOrderByCreatedAtAsc(product, state, PageRequest.of(0, quantity));
        if (availableTransactions.size() < quantity) {
            throw new ResourceNotFoundException("Quantidade solicitada maior que o estoque disponível.");
        }
        for (ProductTransaction tx : availableTransactions) {
            tx.setOutgoingReason(null);
            tx.setOutgoingAt(null);
        }
        List<ProductTransaction> saved = transactionRepository.saveAll(availableTransactions);
        return saved.stream().map(this::entityToResponseDTO).toList();
    }

    private ProductTransactionResponseDTO entityToResponseDTO(ProductTransaction entity) {
        String glazeColor = "sem glasura";
        double glazeQuantity = 0;
        Long bisqueFiringId = null;
        Long glazeFiringId = null;
        if (entity.getGlazeTransaction() != null) {
            glazeColor = entity.getGlazeTransaction().getGlaze().getColor();
            glazeQuantity = entity.getGlazeTransaction().getQuantity();
        }
        if (entity.getBisqueFiring() != null) {
            bisqueFiringId = entity.getBisqueFiring().getId();
        }
        if (entity.getGlazeFiring() != null) {
            glazeFiringId = entity.getGlazeFiring().getId();
        }
        return new ProductTransactionResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getOutgoingAt(),
            entity.getState(),
            entity.getOutgoingReason(),
            entity.getProduct().getName(),
            bisqueFiringId,
            glazeFiringId,
            glazeColor,
            glazeQuantity,
            entity.getCost(),
            entity.getProfit()
        );
    }

    public BigDecimal calculateProductTransactionCost(double transactionWeight) {
        Double totalWeight = batchRepository.getTotalWeight();
        BigDecimal totalCost = batchRepository.getTotalFinalCost();

        if (totalWeight == null || totalWeight == 0) {
            throw new IllegalStateException("Total weight is zero, cannot divide by zero");
        }

        BigDecimal transactionWeightBD = BigDecimal.valueOf(transactionWeight);

        return totalCost
                .multiply(transactionWeightBD)
                .divide(BigDecimal.valueOf(totalWeight), 2, RoundingMode.HALF_UP);
    }

}
