package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.DryingSessionDTO;
import com.jonasdurau.ceramicmanagement.entities.DryingRoom;
import com.jonasdurau.ceramicmanagement.entities.DryingSession;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.DryingRoomRepository;
import com.jonasdurau.ceramicmanagement.repositories.DryingSessionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class DryingSessionService {
    
    @Autowired
    private DryingSessionRepository sessionRepository;

    @Autowired
    private DryingRoomRepository roomRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<DryingSessionDTO> findAllByRoom(Long roomId) {
        DryingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId));
        room.getSessions().size();
        return room.getSessions().stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public DryingSessionDTO findById(Long roomId, Long sessionId) {
        if(!roomRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        return entityToDTO(session);
    }

    @Transactional
    public DryingSessionDTO create(Long roomId, DryingSessionDTO dto) {
        DryingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId));
        DryingSession entity = new DryingSession();
        entity.setHours(dto.getHours());
        entity.setDryingRoom(room);
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = sessionRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public DryingSessionDTO update(Long roomId, Long sessionId, DryingSessionDTO dto) {
        if(!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession entity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        entity.setHours(dto.getHours());
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = sessionRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long roomId, Long sessionId) {
        if(!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession entity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        sessionRepository.delete(entity);
    }

    private DryingSessionDTO entityToDTO(DryingSession entity) {
        DryingSessionDTO dto = new DryingSessionDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setHours(entity.getHours());
        dto.setCostAtTime(entity.getCostAtTime());
        return dto;
    }

    private BigDecimal calculateCostAtTime(DryingSession entity) {
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso eletricidade não cadastrada."));
        Resource gas = resourceRepository.findByCategory(ResourceCategory.GAS)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso gás não cadastrado."));
        BigDecimal electricityCost = electricity.getUnitValue().multiply(BigDecimal.valueOf(entity.getEnergyConsumption()));
        BigDecimal gasCost = gas.getUnitValue().multiply(BigDecimal.valueOf(entity.getGasConsumption()));
        return electricityCost.add(gasCost)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
