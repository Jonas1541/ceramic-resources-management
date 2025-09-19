package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.jonasdurau.ceramicmanagement.dtos.request.EmployeeUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ProductTransactionRequestDTO;
import com.jonasdurau.ceramicmanagement.entities.Employee;
import com.jonasdurau.ceramicmanagement.entities.ProductTransactionEmployeeUsage;
import com.jonasdurau.ceramicmanagement.repositories.BatchRepository;
import com.jonasdurau.ceramicmanagement.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.BisqueFiring;
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

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProductTransactionService transactionService;

    private Product product;
    private ProductTransaction transaction;
    private Employee employee;
    private Long productId = 1L;
    private Long transactionId = 1L;
    private Long employeeId = 1L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(productId);
        product.setName("Vaso Decorativo");
        product.setPrice(new BigDecimal("150.00"));
        product.setWeight(2.5); // Peso para cálculo de custo

        transaction = new ProductTransaction();
        transaction.setId(transactionId);
        transaction.setState(ProductState.GREENWARE);
        transaction.setProduct(product);
        product.getTransactions().add(transaction);
        
        employee = new Employee();
        employee.setId(employeeId);
        employee.setName("Artesão");
        employee.setCostPerHour(new BigDecimal("20.00"));
    }

    @Test
    void findAllByProduct_WhenProductExists_ShouldReturnTransactions() {
        ProductTransactionEmployeeUsage employeeUsage = new ProductTransactionEmployeeUsage();
        employeeUsage.setEmployee(employee);
        transaction.getEmployeeUsages().add(employeeUsage);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByProduct(product)).thenReturn(List.of(transaction));
    
        List<ProductTransactionResponseDTO> result = transactionService.findAllByProduct(productId);
    
        assertFalse(result.isEmpty());
        assertEquals(transactionId, result.getFirst().id());
        assertFalse(result.getFirst().employeeUsages().isEmpty());
    }

    @Test
    void findById_WhenExists_ShouldReturnTransaction() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));

        ProductTransactionResponseDTO result = transactionService.findById(productId, transactionId);

        assertEquals(transactionId, result.id());
    }

    @Test
    void create_WithValidData_ShouldCreateMultipleTransactionsWithCorrectCost() {
        int quantity = 5;
        // Tempo total de mão de obra para produzir 5 peças
        ProductTransactionRequestDTO dto = new ProductTransactionRequestDTO(List.of(new EmployeeUsageRequestDTO(5.0, employeeId)));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        // Mock dos dados do lote para cálculo de custo de material
        when(batchRepository.getTotalWeight()).thenReturn(1000.0);
        when(batchRepository.getTotalFinalCost()).thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.save(any(ProductTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<ProductTransactionResponseDTO> result = transactionService.create(productId, quantity, dto);

        assertEquals(quantity, result.size());
        assertFalse(result.getFirst().employeeUsages().isEmpty());

        // Custo Material = (500.00 * 2.5) / 1000.0 = 1.25
        // Custo Mão de Obra por peça = (5h / 5 peças) * R$20/h = 1h * 20 = 20.00
        // Custo Total por peça = 1.25 + 20.00 = 21.25
        assertEquals(0, new BigDecimal("21.25").compareTo(result.getFirst().cost()));
        verify(transactionRepository, times(quantity)).save(any(ProductTransaction.class));
    }

    @Test
    void create_WhenEmployeeNotFound_ShouldThrowException() {
        ProductTransactionRequestDTO dto = new ProductTransactionRequestDTO(List.of(new EmployeeUsageRequestDTO(5.0, 999L)));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.create(productId, 5, dto));
    }
    
    @Test
    void create_WhenBatchDataIsMissing_ShouldThrowException() {
        ProductTransactionRequestDTO dto = new ProductTransactionRequestDTO(List.of(new EmployeeUsageRequestDTO(5.0, employeeId)));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(batchRepository.getTotalWeight()).thenReturn(0.0); // Causa a divisão por zero
        
        assertThrows(IllegalStateException.class, () -> transactionService.create(productId, 5, dto));
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
    void outgoing_ShouldSetReasonAndTimestamp() {
        ProductOutgoingReason reason = ProductOutgoingReason.SOLD;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(transactionRepository.findByIdAndProduct(transactionId, product)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any())).thenReturn(transaction);

        ProductTransactionResponseDTO result = transactionService.outgoing(productId, transactionId, reason);

        assertNotNull(result.outgoingAt());
        assertEquals(reason, result.outgoingReason());
        assertEquals(0, new BigDecimal("150.00").compareTo(result.profit()));
    }
}