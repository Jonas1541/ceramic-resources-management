package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MachineDTO;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;

@Service
public class MachineService {
    
    @Autowired
    private MachineRepository machineRepository;

    @Transactional(readOnly = true)
    public List<MachineDTO> findAll() {
        List<Machine> list = machineRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public MachineDTO findById(Long id) {
        Machine entity = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public MachineDTO create(MachineDTO dto) {
        if(machineRepository.existsByName(dto.getName())) {
            throw new BusinessException("O nome '" + dto.getName() + "' já existe.");
        }
        Machine entity = dtoToEntity(dto);
        entity = machineRepository.save(entity);
        return entityToDTO(entity);
    }

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
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Machine entity = machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Máquina não encontrada. Id: " + id));
        machineRepository.delete(entity);
    }

    private MachineDTO entityToDTO(Machine entity) {
        MachineDTO dto = new MachineDTO();
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
