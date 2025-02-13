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

import com.jonasdurau.ceramicmanagement.dtos.GlazeFiringDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeFiringService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/kilns/{kilnId}/glaze-firings")
public class GlazeFiringController {
    
    @Autowired
    private GlazeFiringService firingService;

    @GetMapping
    public ResponseEntity<List<GlazeFiringDTO>> findAllByKilnId(@PathVariable Long kilnId) {
        List<GlazeFiringDTO> list = firingService.findAllByKilnId(kilnId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{firingId}")
    public ResponseEntity<GlazeFiringDTO> findById(@PathVariable Long kilnId, @PathVariable Long firingId) {
        GlazeFiringDTO dto = firingService.findById(kilnId, firingId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GlazeFiringDTO> create(@PathVariable Long kilnId, @Valid @RequestBody GlazeFiringDTO dto) {
        GlazeFiringDTO created = firingService.create(kilnId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{firingId}")
    public ResponseEntity<GlazeFiringDTO> update(@PathVariable Long kilnId, @PathVariable Long firingId, @Valid @RequestBody GlazeFiringDTO dto) {
        GlazeFiringDTO updated = firingService.update(kilnId, firingId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{firingId}")
    public ResponseEntity<Void> delete(@PathVariable Long kilnId, @PathVariable Long firingId) {
        firingService.delete(kilnId, firingId);
        return ResponseEntity.noContent().build();
    }
}
