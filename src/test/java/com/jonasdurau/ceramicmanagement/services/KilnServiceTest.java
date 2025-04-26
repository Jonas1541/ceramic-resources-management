package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.jonasdurau.ceramicmanagement.dtos.request.KilnRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.KilnResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.repositories.BisqueFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;

@ExtendWith(MockitoExtension.class)
public class KilnServiceTest {

    @Mock
    private KilnRepository kilnRepository;

    @Mock
    private BisqueFiringRepository bisqueFiringRepository;

    @Mock
    private GlazeFiringRepository glazeFiringRepository;

    @InjectMocks
    private KilnService kilnService;

    private Kiln kiln;
    private KilnRequestDTO requestDTO;
    private Long testId;

    @BeforeEach
    void setUp() {
        testId = 1L;
        
        kiln = new Kiln();
        kiln.setId(testId);
        kiln.setName("Forno Principal");
        kiln.setPower(1500.0);
        kiln.setCreatedAt(Instant.now());
        kiln.setUpdatedAt(Instant.now());

        requestDTO = new KilnRequestDTO("Forno Principal", 1500.0);
    }

    @Test
    void findAll_ShouldReturnListOfKilns() {
        when(kilnRepository.findAll()).thenReturn(List.of(kiln));

        List<KilnResponseDTO> result = kilnService.findAll();

        assertEquals(1, result.size());
        assertEquals(testId, result.getFirst().id());
        verify(kilnRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnKiln() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));

        KilnResponseDTO result = kilnService.findById(testId);

        assertEquals(testId, result.id());
        assertEquals("Forno Principal", result.name());
        verify(kilnRepository).findById(testId);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> kilnService.findById(testId));
        verify(kilnRepository).findById(testId);
    }

    @Test
    void create_WithValidData_ShouldReturnKiln() {
        when(kilnRepository.save(any(Kiln.class))).thenAnswer(invocation -> {
            Kiln saved = invocation.getArgument(0);
            saved.setId(testId);
            return saved;
        });

        KilnResponseDTO result = kilnService.create(requestDTO);

        assertEquals(testId, result.id());
        verify(kilnRepository).save(any(Kiln.class));
    }

    @Test
    void update_WithValidData_ShouldUpdateKiln() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));
        when(kilnRepository.save(any(Kiln.class))).thenReturn(kiln);

        KilnResponseDTO result = kilnService.update(testId, requestDTO);

        assertEquals(testId, result.id());
        assertEquals("Forno Principal", result.name());
        verify(kilnRepository).save(any(Kiln.class));
    }

    @Test
    void update_WhenKilnNotFound_ShouldThrowException() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> kilnService.update(testId, requestDTO));
        verify(kilnRepository, never()).save(any());
    }

    @Test
    void delete_WhenNoFirings_ShouldDeleteKiln() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));
        when(bisqueFiringRepository.existsByKilnId(testId)).thenReturn(false);
        when(glazeFiringRepository.existsByKilnId(testId)).thenReturn(false);

        kilnService.delete(testId);

        verify(kilnRepository).delete(kiln);
    }

    @Test
    void delete_WhenHasBisqueFirings_ShouldThrowException() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));
        when(bisqueFiringRepository.existsByKilnId(testId)).thenReturn(true);

        assertThrows(ResourceDeletionException.class, () -> kilnService.delete(testId));
        verify(kilnRepository, never()).delete(any());
    }

    @Test
    void delete_WhenHasGlazeFirings_ShouldThrowException() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));
        when(glazeFiringRepository.existsByKilnId(testId)).thenReturn(true);

        assertThrows(ResourceDeletionException.class, () -> kilnService.delete(testId));
        verify(kilnRepository, never()).delete(any());
    }

    @Test
    void yearlyReport_WhenKilnExists_ShouldReturnReport() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.of(kiln));

        List<YearReportDTO> reports = kilnService.yearlyReport(testId);

        assertTrue(reports.isEmpty());
        verify(kilnRepository).findById(testId);
    }

    @Test
    void yearlyReport_WhenKilnNotExists_ShouldThrowException() {
        when(kilnRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> kilnService.yearlyReport(testId));
        verify(kilnRepository).findById(testId);
    }
}