package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.KilnDTO;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.repositories.BisqueFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;

@Service
public class KilnService {
    
    @Autowired
    private KilnRepository kilnRepository;

    @Autowired
    private BisqueFiringRepository bisqueFiringRepository;

    @Autowired
    private GlazeFiringRepository glazeFiringRepository;

    @Transactional(readOnly = true)
    public List<KilnDTO> findAll() {
        List<Kiln> list = kilnRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public KilnDTO findById(Long id) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public KilnDTO create(KilnDTO dto) {
        Kiln entity = new Kiln();
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        entity = kilnRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public KilnDTO update(Long id, KilnDTO dto) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. id: " + id));
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        entity = kilnRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + id));
        boolean hasBisqueFiring = bisqueFiringRepository.existsByKilnId(id);
        boolean hasGlazeFiring = glazeFiringRepository.existsByKilnId(id);
        if(hasBisqueFiring || hasGlazeFiring) {
            throw new ResourceDeletionException("O forno não pode ser deletado pois possui queimas associadas.");
        }
        kilnRepository.delete(entity);
    }

    private KilnDTO entityToDTO(Kiln entity) {
        KilnDTO dto = new KilnDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setPower(entity.getPower());
        dto.setTotalBisqueFirings(entity.getTotalBisqueFirings());
        dto.setTotalBisqueFiringsCost(entity.getTotalBisqueFiringsCost());
        dto.setTotalGlazeFirings(entity.getTotalGlazeFirings());
        dto.setTotalGlazeFiringsCost(entity.getTotalGlazeFiringsCost());
        return dto;
    }
}
