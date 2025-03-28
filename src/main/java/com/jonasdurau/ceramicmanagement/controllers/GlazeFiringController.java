package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeFiringService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/kilns/{kilnId}/glaze-firings")
public class GlazeFiringController {
    
    @Autowired
    private GlazeFiringService firingService;

    @GetMapping
    public ResponseEntity<List<FiringListDTO>> findAllByKilnId(@PathVariable Long kilnId) {
        List<FiringListDTO> list = firingService.findAllByKilnId(kilnId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{firingId}")
    public ResponseEntity<GlazeFiringResponseDTO> findById(@PathVariable Long kilnId, @PathVariable Long firingId) {
        GlazeFiringResponseDTO dto = firingService.findById(kilnId, firingId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GlazeFiringResponseDTO> create(@PathVariable Long kilnId, @Valid @RequestBody GlazeFiringRequestDTO dto) {
        GlazeFiringResponseDTO created = firingService.create(kilnId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{firingId}")
    public ResponseEntity<GlazeFiringResponseDTO> update(@PathVariable Long kilnId, @PathVariable Long firingId, @Valid @RequestBody GlazeFiringRequestDTO dto) {
        GlazeFiringResponseDTO updated = firingService.update(kilnId, firingId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{firingId}")
    public ResponseEntity<Void> delete(@PathVariable Long kilnId, @PathVariable Long firingId) {
        firingService.delete(kilnId, firingId);
        return ResponseEntity.noContent().build();
    }
}
