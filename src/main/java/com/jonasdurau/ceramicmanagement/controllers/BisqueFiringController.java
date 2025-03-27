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

import com.jonasdurau.ceramicmanagement.dtos.request.BisqueFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BisqueFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.services.BisqueFiringService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/kilns/{kilnId}/bisque-firings")
public class BisqueFiringController {
    
    @Autowired
    private BisqueFiringService firingService;

    @GetMapping
    public ResponseEntity<List<BisqueFiringResponseDTO>> findAllByKilnId(@PathVariable Long kilnId) {
        List<BisqueFiringResponseDTO> list = firingService.findAllByKilnId(kilnId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{firingId}")
    public ResponseEntity<BisqueFiringResponseDTO> findById(@PathVariable Long kilnId, @PathVariable Long firingId) {
        BisqueFiringResponseDTO dto = firingService.findById(kilnId, firingId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BisqueFiringResponseDTO> create(@PathVariable Long kilnId, @Valid @RequestBody BisqueFiringRequestDTO dto) {
        BisqueFiringResponseDTO created = firingService.create(kilnId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{firingId}")
    public ResponseEntity<BisqueFiringResponseDTO> update(@PathVariable Long kilnId, @PathVariable Long firingId, @Valid @RequestBody BisqueFiringRequestDTO dto) {
        BisqueFiringResponseDTO updated = firingService.update(kilnId, firingId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{firingId}")
    public ResponseEntity<Void> delete(@PathVariable Long kilnId, @PathVariable Long firingId) {
        firingService.delete(kilnId, firingId);
        return ResponseEntity.noContent().build();
    }
}
