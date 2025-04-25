package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ProductRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductLine;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.repositories.ProductLineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTypeRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository typeRepository;

    @Mock
    private ProductLineRepository lineRepository;

    @Mock
    private ProductTransactionRepository transactionRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO requestDTO;
    private Long testId;
    private ProductType productType;
    private ProductLine productLine;

    @BeforeEach
    void setUp() {
        testId = 1L;

        productType = new ProductType();
        productType.setId(1L);
        productType.setName("Vaso");

        productLine = new ProductLine();
        productLine.setId(1L);
        productLine.setName("Coleção Verão");

        product = new Product();
        product.setId(testId);
        product.setName("Vaso Decorativo");
        product.setPrice(new BigDecimal("150.00"));
        product.setHeight(30.0);
        product.setLength(20.0);
        product.setWidth(20.0);
        product.setType(productType);
        product.setLine(productLine);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        requestDTO = new ProductRequestDTO(
            "Vaso Decorativo",
            new BigDecimal("150.00"),
            30.0,
            20.0,
            20.0,
            1L,
            1L
        );
    }

    @Test
    void findAll_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());
        verify(productRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));

        ProductResponseDTO result = productService.findById(testId);

        assertEquals(testId, result.id());
        assertEquals("Vaso Decorativo", result.name());
        verify(productRepository).findById(testId);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(testId));
        verify(productRepository).findById(testId);
    }

    @Test
    void create_WithValidData_ShouldReturnProduct() {
        when(typeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(productLine));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(testId);
            return savedProduct;
        });

        ProductResponseDTO result = productService.create(requestDTO);

        assertEquals(testId, result.id());
        assertEquals("Vaso Decorativo", result.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_WithInvalidType_ShouldThrowException() {
        when(typeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.create(requestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_WithInvalidLine_ShouldThrowException() {
        when(typeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(lineRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.create(requestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));
        when(typeRepository.findById(1L)).thenReturn(Optional.of(productType));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(productLine));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.update(testId, requestDTO);

        assertEquals(testId, result.id());
        assertEquals("Vaso Decorativo", result.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(testId, requestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_WhenNoTransactions_ShouldDeleteProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));
        when(transactionRepository.existsByProductId(testId)).thenReturn(false);

        productService.delete(testId);

        verify(productRepository).delete(product);
    }

    @Test
    void delete_WhenHasTransactions_ShouldThrowException() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));
        when(transactionRepository.existsByProductId(testId)).thenReturn(true);

        assertThrows(ResourceDeletionException.class, () -> productService.delete(testId));
        verify(productRepository, never()).delete(any());
    }

    @Test
    void yearlyReport_WhenProductExists_ShouldReturnReport() {
        ProductTransaction tx = new ProductTransaction();
        tx.setProduct(product);
        tx.setCreatedAt(Instant.now());
        tx.setOutgoingReason(ProductOutgoingReason.SOLD);
        
        product.getTransactions().add(tx);

        when(productRepository.findById(testId)).thenReturn(Optional.of(product));

        List<YearReportDTO> reports = productService.yearlyReport(testId);

        assertFalse(reports.isEmpty());
        verify(productRepository).findById(testId);
    }

    @Test
    void yearlyReport_WhenProductNotExists_ShouldThrowException() {
        when(productRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.yearlyReport(testId));
        verify(productRepository).findById(testId);
    }
}