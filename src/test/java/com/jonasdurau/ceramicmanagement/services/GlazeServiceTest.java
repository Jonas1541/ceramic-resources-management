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

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.GlazeListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeResourceUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Glaze;
import com.jonasdurau.ceramicmanagement.entities.GlazeMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.GlazeResourceUsage;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.GlazeMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeResourceUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@ExtendWith(MockitoExtension.class)
public class GlazeServiceTest {

    @Mock
    private GlazeRepository glazeRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private GlazeTransactionRepository transactionRepository;

    @Mock
    private GlazeResourceUsageRepository glazeResourceUsageRepository;

    @Mock
    private GlazeMachineUsageRepository glazeMachineUsageRepository;

    @InjectMocks
    private GlazeService glazeService;

    private Glaze glaze;
    private GlazeRequestDTO requestDTO;
    private Long testId;
    private Resource electricityResource;

    @BeforeEach
    void setUp() {
        testId = 1L;

        electricityResource = new Resource();
        electricityResource.setId(1L);
        electricityResource.setCategory(ResourceCategory.ELECTRICITY);
        electricityResource.setUnitValue(new BigDecimal("0.50"));

        glaze = new Glaze();
        glaze.setId(testId);
        glaze.setColor("Azul");
        glaze.setUnitValue(new BigDecimal("100.00"));
        glaze.setCreatedAt(Instant.now());
        glaze.setUpdatedAt(Instant.now());

        requestDTO = new GlazeRequestDTO(
            "Azul",
            new BigDecimal("100.00"),
            List.of(new GlazeResourceUsageRequestDTO(1L, 2.0)),
            List.of(new GlazeMachineUsageRequestDTO(1L, 5.0))
        );
    }

    @Test
    void findAll_ShouldReturnListOfGlazes() {
        when(glazeRepository.findAll()).thenReturn(List.of(glaze));

        List<GlazeListDTO> result = glazeService.findAll();

        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());
        verify(glazeRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnGlaze() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.of(glaze));

        GlazeResponseDTO result = glazeService.findById(testId);

        assertEquals(testId, result.id());
        verify(glazeRepository).findById(testId);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glazeService.findById(testId));
        verify(glazeRepository).findById(testId);
    }

    @Test
    void create_WithValidData_ShouldReturnGlaze() {

        Resource mockResource = new Resource();
        mockResource.setUnitValue(new BigDecimal("2.00"));

        Machine mockMachine = new Machine();
        mockMachine.setId(testId);
        mockMachine.setPower(1500.0);
        
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(mockResource));
        when(machineRepository.findById(1L)).thenReturn(Optional.of(mockMachine));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY))
            .thenReturn(Optional.of(electricityResource));
        when(glazeRepository.save(any())).thenAnswer(invocation -> {
            Glaze saved = invocation.getArgument(0);
            saved.setId(testId);
            return saved;
        });
    
        GlazeResponseDTO result = glazeService.create(requestDTO);
    
        assertEquals(testId, result.id());
        assertNotNull(result.unitCost());
        verify(glazeRepository).save(any());
    }

    @Test
    void create_WithMissingResource_ShouldThrowException() {
        when(resourceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glazeService.create(requestDTO));
        verify(glazeRepository, never()).save(any());
    }

    @Test
    void create_WithMissingElectricityResource_ShouldThrowException() {

        Machine machine = new Machine();
        machine.setId(1L);
        machine.setPower(1500.0);
    
        Resource resource = new Resource();
        resource.setUnitValue(new BigDecimal("2.00"));
    
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.empty());
    
        assertThrows(BusinessException.class, () -> glazeService.create(requestDTO));
        verify(glazeRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateGlaze() {

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setUnitValue(new BigDecimal("2.00"));
    
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setPower(1500.0);
    
        when(glazeRepository.findById(testId)).thenReturn(Optional.of(glaze));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY))
            .thenReturn(Optional.of(electricityResource));
        when(glazeRepository.save(any())).thenReturn(glaze);
    
        GlazeResponseDTO result = glazeService.update(testId, requestDTO);
    
        assertEquals(testId, result.id());
        verify(glazeRepository).save(any());
    }

    @Test
    void update_WhenGlazeNotFound_ShouldThrowException() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glazeService.update(testId, requestDTO));
        verify(glazeRepository, never()).save(any());
    }

    @Test
    void delete_WhenNoTransactions_ShouldDeleteGlaze() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.of(glaze));
        when(transactionRepository.existsByGlazeId(testId)).thenReturn(false);

        glazeService.delete(testId);

        verify(glazeRepository).delete(glaze);
    }

    @Test
    void delete_WhenHasTransactions_ShouldThrowException() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.of(glaze));
        when(transactionRepository.existsByGlazeId(testId)).thenReturn(true);

        assertThrows(ResourceDeletionException.class, () -> glazeService.delete(testId));
        verify(glazeRepository, never()).delete(any());
    }

    @Test
    void yearlyReport_WhenGlazeExists_ShouldReturnReport() {
        when(glazeRepository.findById(testId)).thenReturn(Optional.of(glaze));

        List<YearReportDTO> reports = glazeService.yearlyReport(testId);

        assertTrue(reports.isEmpty());
        verify(glazeRepository).findById(testId);
    }

    @Test
    void recalculateGlazesByResource_ShouldUpdateGlazes() {
        GlazeResourceUsage usage = new GlazeResourceUsage();
        usage.setGlaze(glaze);
        when(glazeResourceUsageRepository.findByResourceId(testId)).thenReturn(List.of(usage));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricityResource));

        glazeService.recalculateGlazesByResource(testId);

        verify(glazeRepository).save(glaze);
    }

    @Test
    void recalculateGlazesByMachine_ShouldUpdateGlazes() {
        GlazeMachineUsage usage = new GlazeMachineUsage();
        usage.setGlaze(glaze);
        when(glazeMachineUsageRepository.findByMachineId(testId)).thenReturn(List.of(usage));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricityResource));

        glazeService.recalculateGlazesByMachine(testId);

        verify(glazeRepository).save(glaze);
    }
}