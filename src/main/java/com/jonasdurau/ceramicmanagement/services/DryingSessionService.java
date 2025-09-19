package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.request.DryingSessionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.EmployeeUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DryingSessionResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.EmployeeUsageResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.DryingRoom;
import com.jonasdurau.ceramicmanagement.entities.DryingSession;
import com.jonasdurau.ceramicmanagement.entities.DryingSessionEmployeeUsage;
import com.jonasdurau.ceramicmanagement.entities.Employee;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import com.jonasdurau.ceramicmanagement.repositories.DryingRoomRepository;
import com.jonasdurau.ceramicmanagement.repositories.DryingSessionRepository;
import com.jonasdurau.ceramicmanagement.repositories.EmployeeRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@Service
public class DryingSessionService implements DependentCrudService<DryingSessionResponseDTO, DryingSessionRequestDTO, DryingSessionResponseDTO, Long>{
    
    @Autowired
    private DryingSessionRepository sessionRepository;

    @Autowired
    private DryingRoomRepository roomRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<DryingSessionResponseDTO> findAllByParentId(Long roomId) {
        DryingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId));
        return room.getSessions().stream().map(this::entityToResponseDTO).toList();
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public DryingSessionResponseDTO findById(Long roomId, Long sessionId) {
        if(!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        return entityToResponseDTO(session);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public DryingSessionResponseDTO create(Long roomId, DryingSessionRequestDTO dto) {
        DryingRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId));
        
        DryingSession entity = new DryingSession();
        entity.setHours(dto.hours());
        entity.setDryingRoom(room);

        for (EmployeeUsageRequestDTO euDTO : dto.employeeUsages()) {
            Employee employee = employeeRepository.findById(euDTO.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. Id: " + euDTO.employeeId()));
            DryingSessionEmployeeUsage eu = new DryingSessionEmployeeUsage();
            eu.setDryingSession(entity);
            eu.setEmployee(employee);
            eu.setUsageTime(euDTO.usageTime());
            entity.getEmployeeUsages().add(eu);
        }

        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = sessionRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public DryingSessionResponseDTO update(Long roomId, Long sessionId, DryingSessionRequestDTO dto) {
        if(!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession entity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        
        entity.setHours(dto.hours());

        // Atualiza funcionários
        Map<Long, DryingSessionEmployeeUsage> existingEmployeeUsages = entity.getEmployeeUsages().stream()
            .collect(Collectors.toMap(eu -> eu.getEmployee().getId(), eu -> eu));
        Set<Long> updatedEmployeeIds = new HashSet<>();
        
        for (EmployeeUsageRequestDTO euDTO : dto.employeeUsages()) {
            DryingSessionEmployeeUsage existingEu = existingEmployeeUsages.get(euDTO.employeeId());
            if (existingEu != null) {
                existingEu.setUsageTime(euDTO.usageTime());
                updatedEmployeeIds.add(euDTO.employeeId());
            } else {
                Employee employee = employeeRepository.findById(euDTO.employeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. Id: " + euDTO.employeeId()));
                DryingSessionEmployeeUsage newEu = new DryingSessionEmployeeUsage();
                newEu.setDryingSession(entity);
                newEu.setEmployee(employee);
                newEu.setUsageTime(euDTO.usageTime());
                entity.getEmployeeUsages().add(newEu);
                updatedEmployeeIds.add(euDTO.employeeId());
            }
        }
        
        List<DryingSessionEmployeeUsage> euToRemove = entity.getEmployeeUsages().stream()
            .filter(eu -> !updatedEmployeeIds.contains(eu.getEmployee().getId()))
            .collect(Collectors.toList());
        entity.getEmployeeUsages().removeAll(euToRemove);
        
        entity.setCostAtTime(calculateCostAtTime(entity));
        entity = sessionRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public void delete(Long roomId, Long sessionId) {
        if(!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Estufa não encontrada. Id: " + roomId);
        }
        DryingSession entity = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada. Id: " + sessionId));
        sessionRepository.delete(entity);
    }

    private DryingSessionResponseDTO entityToResponseDTO(DryingSession entity) {
        List<EmployeeUsageResponseDTO> employeeUsageDTOs = entity.getEmployeeUsages().stream()
            .map(eu -> new EmployeeUsageResponseDTO(
                eu.getEmployee().getId(),
                eu.getEmployee().getName(),
                eu.getUsageTime()
            ))
            .collect(Collectors.toList());

        return new DryingSessionResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getHours(),
            employeeUsageDTOs,
            entity.getCostAtTime()
        );
    }

    private BigDecimal calculateCostAtTime(DryingSession entity) {
        Resource electricity = resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso eletricidade não cadastrada."));
        Resource gas = resourceRepository.findByCategory(ResourceCategory.GAS)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso gás não cadastrado."));
        
        BigDecimal electricityCost = electricity.getUnitValue().multiply(BigDecimal.valueOf(entity.getEnergyConsumption()));
        BigDecimal gasCost = gas.getUnitValue().multiply(BigDecimal.valueOf(entity.getGasConsumption()));

        BigDecimal employeeCost = entity.getEmployeeUsages().stream()
            .map(usage -> usage.getEmployee().getCostPerHour()
                .multiply(BigDecimal.valueOf(usage.getUsageTime())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return electricityCost.add(gasCost).add(employeeCost)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
