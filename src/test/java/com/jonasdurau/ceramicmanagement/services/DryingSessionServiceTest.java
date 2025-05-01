package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.request.DryingSessionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DryingSessionResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.*;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.DryingRoomRepository;
import com.jonasdurau.ceramicmanagement.repositories.DryingSessionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@ExtendWith(MockitoExtension.class)
public class DryingSessionServiceTest {

    @Mock
    private DryingSessionRepository sessionRepository;

    @Mock
    private DryingRoomRepository roomRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private DryingSessionService dryingSessionService;

    private DryingRoom dryingRoom;
    private DryingSession session;
    private Resource electricity;
    private Resource gas;
    private Long roomId = 1L;
    private Long sessionId = 1L;

    @BeforeEach
    void setUp() {
        dryingRoom = new DryingRoom();
        dryingRoom.setId(roomId);
        dryingRoom.setName("Estufa Principal");
        dryingRoom.setGasConsumptionPerHour(2.5);

        session = new DryingSession();
        session.setId(sessionId);
        session.setHours(8.0);
        session.setDryingRoom(dryingRoom);

        Machine machine = new Machine();
        machine.setPower(2.0);
        dryingRoom.getMachines().add(machine);

        electricity = new Resource();
        electricity.setCategory(ResourceCategory.ELECTRICITY);
        electricity.setUnitValue(new BigDecimal("0.75"));

        gas = new Resource();
        gas.setCategory(ResourceCategory.GAS);
        gas.setUnitValue(new BigDecimal("4.00"));
    }

    @Test
    void findAllByParentId_ShouldReturnSessionsList() {
        dryingRoom.getSessions().add(session);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(dryingRoom));

        List<DryingSessionResponseDTO> result = dryingSessionService.findAllByParentId(roomId);

        assertEquals(1, result.size());
        assertEquals(sessionId, result.getFirst().id());
    }

    @Test
    void findAllByParentId_WhenRoomNotFound_ShouldThrowException() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.findAllByParentId(roomId));
    }

    @Test
    void findById_WhenValidIds_ShouldReturnSession() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        DryingSessionResponseDTO result = dryingSessionService.findById(roomId, sessionId);

        assertEquals(sessionId, result.id());
        verify(sessionRepository).findById(sessionId);
    }

    @Test
    void findById_WhenSessionNotExists_ShouldThrowException() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.findById(roomId, sessionId));
    }

    @Test
    void findById_WhenRoomNotExists_ShouldThrowException() {
        when(roomRepository.existsById(roomId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.findById(roomId, sessionId));
    }

    @Test
    void create_WithValidData_ShouldPersistSession() {

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(dryingRoom));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        when(sessionRepository.save(any(DryingSession.class))).thenAnswer(invocation -> {
            DryingSession saved = invocation.getArgument(0);
            saved.setId(sessionId);
            return saved;
        });

        DryingSessionResponseDTO result = dryingSessionService.create(roomId, new DryingSessionRequestDTO(8.0));

        assertEquals(new BigDecimal("88.88"), result.costAtTime());
    }


    @Test
    void create_WhenRoomNotFound_ShouldThrowException() {
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.create(roomId, new DryingSessionRequestDTO(8.0)));
    }

    @Test
    void update_ShouldRecalculateCost() {

        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DryingSessionResponseDTO result = dryingSessionService.update(roomId, sessionId, new DryingSessionRequestDTO(10.0));

        assertEquals(new BigDecimal("111.10"), result.costAtTime());
    }

    @Test
    void update_WhenElectricityMissing_ShouldThrowException() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.update(roomId, sessionId, new DryingSessionRequestDTO(10.0)));
    }

    @Test
    void update_WhenGasMissing_ShouldThrowException() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.update(roomId, sessionId, new DryingSessionRequestDTO(10.0)));
    }

    @Test
    void delete_WhenValidIds_ShouldRemoveSession() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        dryingSessionService.delete(roomId, sessionId);

        verify(sessionRepository).delete(session);
    }

    @Test
    void delete_WhenSessionNotExists_ShouldThrowException() {
        when(roomRepository.existsById(roomId)).thenReturn(true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dryingSessionService.delete(roomId, sessionId));
    }
}