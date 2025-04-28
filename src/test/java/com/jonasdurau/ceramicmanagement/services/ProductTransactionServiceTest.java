package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.BisqueFiring;
import com.jonasdurau.ceramicmanagement.entities.GlazeFiring;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class ProductTransactionServiceTest {

    @Mock
    private ProductTransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductTransactionService transactionService;

    private Product product;
    private ProductTransaction transaction;
    private Long productId;
    private Long transactionId;

    @BeforeEach
    void setUp() {
        productId = 1L;
        transactionId = 1L;

        product = new Product();
        product.setId(productId);
        product.setName("Vaso Decorativo");
        product.setPrice(new BigDecimal("150.00"));

        transaction = new ProductTransaction();
        transaction.setId(transactionId);
        transaction.setState(ProductState.GREENWARE);
        transaction.setProduct(product);
        product.getTransactions().add(transaction);
    }

    @Test
    void findAllByProduct_WhenProductExists_ShouldReturnTransactions() {

        List<ProductTransaction> transactions = List.of(transaction);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByProduct(product)).thenReturn(transactions);
    
        List<ProductTransactionResponseDTO> result = transactionService.findAllByProduct(productId);
    
        assertFalse(result.isEmpty(), "Deveria retornar transações associadas ao produto");
        assertEquals(transactionId, result.getFirst().id(), "ID da transação incorreto");
    }

    @Test
    void findAllByProduct_WhenProductNotFound_ShouldThrowBusinessException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> transactionService.findAllByProduct(productId));
    }

    @Test
    void findAllByState_ShouldReturnFilteredTransactions() {
        when(transactionRepository.findByState(ProductState.GREENWARE)).thenReturn(List.of(transaction));

        List<ProductTransactionResponseDTO> result = transactionService.findAllByState(ProductState.GREENWARE);

        assertFalse(result.isEmpty());
        assertEquals(ProductState.GREENWARE, result.getFirst().state());
    }

    @Test
    void findById_WhenExists_ShouldReturnTransaction() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));

        ProductTransactionResponseDTO result = transactionService.findById(productId, transactionId);

        assertEquals(transactionId, result.id());
    }

    @Test
    void findById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById(productId, transactionId));
    }

    @Test
    void findById_WhenTransactionNotFound_ShouldThrowException() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById(productId, transactionId));
    }

    @Test
    void create_ShouldCreateMultipleTransactions() {
        int quantity = 5;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.save(any())).thenAnswer(invocation -> {
            ProductTransaction t = invocation.getArgument(0);
            t.setId((long) (Math.random() * 1000));
            return t;
        });

        List<ProductTransactionResponseDTO> result = transactionService.create(productId, quantity);

        assertEquals(quantity, result.size());
        verify(transactionRepository, times(quantity)).save(any());
    }

    @Test
    void delete_WhenNoFirings_ShouldDelete() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));

        transactionService.delete(productId, transactionId);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void delete_WhenBisqueFiringExists_ShouldBlockDeletion() {
        transaction.setBisqueFiring(new BisqueFiring());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));

        assertThrows(ResourceDeletionException.class, () -> transactionService.delete(productId, transactionId));
    }

    @Test
    void delete_WhenGlazeFiringExists_ShouldBlockDeletion() {
        transaction.setGlazeFiring(new GlazeFiring());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));

        assertThrows(ResourceDeletionException.class, () -> transactionService.delete(productId, transactionId));
    }

    @Test
    void outgoing_ShouldSetReasonAndTimestamp() {
        ProductOutgoingReason reason = ProductOutgoingReason.SOLD;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any())).thenReturn(transaction);

        ProductTransactionResponseDTO result = transactionService.outgoing(productId, transactionId, reason);

        assertNotNull(result.outgoingAt());
        assertEquals(reason, result.outgoingReason());
        assertEquals(new BigDecimal("150.00"), result.profit());
    }

    @Test
    void outgoing_WhenDefectDisposal_ShouldSetZeroProfit() {
        ProductOutgoingReason reason = ProductOutgoingReason.DEFECT_DISPOSAL;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any())).thenReturn(transaction);

        ProductTransactionResponseDTO result = transactionService.outgoing(productId, transactionId, reason);

        assertEquals(BigDecimal.ZERO, result.profit());
    }
}