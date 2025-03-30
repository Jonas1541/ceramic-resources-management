package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MachineDTO;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.repositories.BatchMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.DryingRoomRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;

@Service
public class MachineService implements IndependentCrudService<MachineDTO, MachineDTO, MachineDTO, Long>{
    
    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private BatchMachineUsageRepository batchMachineUsageRepository;

    @Autowired
    private GlazeMachineUsageRepository glazeMachineUsageRepository;

    @Autowired
    private DryingRoomRepository dryingRoomRepository;

    @Autowired
    private GlazeService glazeService;

    @Override
    @Transactional(readOnly = true)
    public List<MachineDTO> findAll() {
        List<Machine> list = machineRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MachineDTO findById(Long id) {
        Machine entity = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + id));
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public MachineDTO create(MachineDTO dto) {
        if(machineRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome '" + dto.getName() + "' já existe.");
        }
        Machine entity = dtoToEntity(dto);
        entity = machineRepository.save(entity);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public MachineDTO update(Long id, MachineDTO dto) {
        Machine entity = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + id));
        String newName = dto.getName();
        String oldName = entity.getName();
        if (!oldName.equals(newName) && machineRepository.existsByName(newName)) {
            throw new BusinessException("O nome '" + newName + "' já existe.");
        }
        entity.setName(newName);
        entity.setPower(dto.getPower());
        entity = machineRepository.save(entity);
        glazeService.recalculateGlazesByMachine(id);
        return entityToDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Machine entity = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + id));
        boolean hasBatchUsages = batchMachineUsageRepository.existsByMachineId(id);
        boolean hasGlazeUsages = glazeMachineUsageRepository.existsByMachineId(id);
        boolean hasDryingRooms = dryingRoomRepository.existsByMachinesId(id);
        if (hasBatchUsages) {
            throw new ResourceDeletionException("Não é possível deletar a máquina com id " + id + " pois ela tem bateladas associadas.");
        }
        if (hasGlazeUsages) {
            throw new ResourceDeletionException("Não é possível deletar a máquina com id " + id + " pois ela tem glasuras associadas.");
        }
        if (hasDryingRooms) {
            throw new ResourceDeletionException("Não é possível deletar a máquina com id " + id + "pois ela tem estufas associadas.");
        }
        machineRepository.delete(entity);
    }

    private MachineDTO entityToDTO(Machine entity) {
        MachineDTO dto = new MachineDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setPower(entity.getPower());
        return dto;
    }

    private Machine dtoToEntity(MachineDTO dto) {
        Machine entity = new Machine();
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        return entity;
    }
}
