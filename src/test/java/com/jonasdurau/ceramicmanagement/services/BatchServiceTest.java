package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.BatchListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BatchResourceUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BatchResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Batch;
import com.jonasdurau.ceramicmanagement.entities.BatchMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.BatchResourceUsage;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.BatchRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private ResourceTransactionRepository resourceTransactionRepository;

    @InjectMocks
    private BatchService batchService;

    private Batch batch;
    private BatchRequestDTO requestDTO;
    private Long testId;
    private Resource waterResource;
    private Resource electricityResource;
    private Machine machine;
    private Resource commonResource;

    @BeforeEach
    void setUp() {
        testId = 1L;
        
        waterResource = new Resource();
        waterResource.setId(1L);
        waterResource.setName("Água");
        waterResource.setCategory(ResourceCategory.WATER);
        waterResource.setUnitValue(new BigDecimal("0.05"));

        electricityResource = new Resource();
        electricityResource.setId(2L);
        electricityResource.setName("Eletricidade");
        electricityResource.setCategory(ResourceCategory.ELECTRICITY);
        electricityResource.setUnitValue(new BigDecimal("0.50"));

        machine = new Machine();
        machine.setId(1L);
        machine.setName("machine");
        machine.setPower(150.0);

        commonResource = new Resource();
        commonResource.setId(3L);
        commonResource.setName("Argila");
        commonResource.setCategory(ResourceCategory.RAW_MATERIAL);
        commonResource.setUnitValue(new BigDecimal("2.50"));

        batch = new Batch();
        batch.setId(testId);
        batch.setCreatedAt(Instant.now());
        batch.setUpdatedAt(Instant.now());
        
        BatchResourceUsage resourceUsage = new BatchResourceUsage();
        resourceUsage.setId(testId);
        resourceUsage.setInitialQuantity(10.0);
        resourceUsage.setUmidity(0.5);
        resourceUsage.setAddedQuantity(5.0);
        resourceUsage.setResource(commonResource);
        resourceUsage.setBatch(batch);
        resourceUsage.setTotalCostAtTime(new BigDecimal("100.00"));
        batch.getResourceUsages().add(resourceUsage);

        BatchMachineUsage machineUsage = new BatchMachineUsage();
        machineUsage.setId(testId);
        machineUsage.setUsageTime(2.0);
        machineUsage.setMachine(machine);
        machineUsage.setBatch(batch);
        batch.getMachineUsages().add(machineUsage);

        batch.setBatchTotalWaterCostAtTime(new BigDecimal("50.00"));
        batch.setResourceTotalCostAtTime(new BigDecimal("200.00"));
        batch.setMachinesEnergyConsumptionCostAtTime(new BigDecimal("150.00"));
        batch.setBatchFinalCostAtTime(new BigDecimal("400.00"));

        requestDTO = new BatchRequestDTO(
            List.of(new BatchResourceUsageRequestDTO(3L, 10.0, 50.0, 5.0)),
            List.of(new BatchMachineUsageRequestDTO(1L, 2.0))
        );
    }

    @Test
    void findAll_ShouldReturnListOfBatches() {
        when(batchRepository.findAll()).thenReturn(List.of(batch));

        List<BatchListDTO> result = batchService.findAll();

        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());
        verify(batchRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnBatch() {
        when(batchRepository.findById(testId)).thenReturn(Optional.of(batch));

        BatchResponseDTO result = batchService.findById(testId);

        assertEquals(testId, result.id());
        assertEquals(new BigDecimal("100.00"), result.resourceUsages().getFirst().totalCost());
        verify(batchRepository).findById(testId);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(batchRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.findById(testId));
        verify(batchRepository).findById(testId);
    }

    @Test
    void create_WithValidData_ShouldReturnSavedBatch() {
        when(resourceRepository.findById(3L)).thenReturn(Optional.of(commonResource));
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(resourceRepository.findByCategory(ResourceCategory.WATER)).thenReturn(Optional.of(waterResource));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricityResource));
        when(batchRepository.save(any(Batch.class))).thenAnswer(invocation -> {
            Batch savedBatch = invocation.getArgument(0);
            savedBatch.setId(testId);
            return savedBatch;
        });

        BatchResponseDTO result = batchService.create(requestDTO);

        assertEquals(testId, result.id());
        verify(batchRepository).save(any(Batch.class));
    }

    @Test
    void create_WithMissingResource_ShouldThrowException() {
        when(resourceRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.create(requestDTO));
        verify(batchRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateBatch() {
        when(batchRepository.findById(testId)).thenReturn(Optional.of(batch));
        when(resourceRepository.findByCategory(ResourceCategory.WATER)).thenReturn(Optional.of(waterResource));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricityResource));
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);

        BatchResponseDTO result = batchService.update(testId, requestDTO);

        assertEquals(testId, result.id());
        verify(batchRepository).save(any(Batch.class));
    }

    @Test
    void update_WhenResourceNotFound_ShouldThrowResourceNotFoundException() {
        BatchRequestDTO invalidRequest = new BatchRequestDTO(
            List.of(new BatchResourceUsageRequestDTO(999L, 10.0, 50.0, 5.0)),
            List.of(new BatchMachineUsageRequestDTO(1L, 2.0))
        );

        when(batchRepository.findById(testId)).thenReturn(Optional.of(batch));
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.update(testId, invalidRequest));
        
        verify(resourceRepository).findById(999L);
        verify(batchRepository, never()).save(any());
    }

    @Test
    void update_WhenMachineNotFound_ShouldThrowResourceNotFoundException() {
        BatchRequestDTO invalidRequest = new BatchRequestDTO(
            List.of(new BatchResourceUsageRequestDTO(3L, 10.0, 50.0, 5.0)),
            List.of(new BatchMachineUsageRequestDTO(999L, 2.0))
        );

        when(batchRepository.findById(testId)).thenReturn(Optional.of(batch));
        when(machineRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.update(testId, invalidRequest));
        
        verify(machineRepository).findById(999L);
        verify(batchRepository, never()).save(any());
    }

    @Test
    void update_WhenBatchNotFound_ShouldThrowResourceNotFoundException() {
        when(batchRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.update(testId, requestDTO));
        
        verify(batchRepository).findById(testId);
        verify(batchRepository, never()).save(any());
    }

    @Test
    void delete_WhenExists_ShouldDeleteBatch() {
        when(batchRepository.findById(testId)).thenReturn(Optional.of(batch));

        batchService.delete(testId);

        verify(batchRepository).delete(batch);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(batchRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.delete(testId));
        verify(batchRepository, never()).delete(any());
    }

    @Test
    void yearlyReport_ShouldReturnAggregatedData() {
        batch.setCreatedAt(Instant.now().atZone(ZoneId.systemDefault()).toInstant());
        when(batchRepository.findAll()).thenReturn(List.of(batch));

        List<YearReportDTO> reports = batchService.yearlyReport();

        assertTrue(reports.size() > 0);
        verify(batchRepository).findAll();
    }
}