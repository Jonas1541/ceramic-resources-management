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

import com.jonasdurau.ceramicmanagement.dtos.BisqueFiringDTO;
import com.jonasdurau.ceramicmanagement.services.BisqueFiringService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/kilns/{kilnId}/bisque-firings")
public class BisqueFiringController {
    
    @Autowired
    private BisqueFiringService firingService;

    @GetMapping
    public ResponseEntity<List<BisqueFiringDTO>> findAllByKilnId(@PathVariable Long kilnId) {
        List<BisqueFiringDTO> list = firingService.findAllByKilnId(kilnId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{firingId}")
    public ResponseEntity<BisqueFiringDTO> findById(@PathVariable Long kilnId, @PathVariable Long firingId) {
        BisqueFiringDTO dto = firingService.findById(kilnId, firingId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BisqueFiringDTO> create(@PathVariable Long kilnId, @Valid @RequestBody BisqueFiringDTO dto) {
        BisqueFiringDTO created = firingService.create(kilnId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{firingId}")
    public ResponseEntity<BisqueFiringDTO> update(@PathVariable Long kilnId, @PathVariable Long firingId, @Valid @RequestBody BisqueFiringDTO dto) {
        BisqueFiringDTO updated = firingService.update(kilnId, firingId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{firingId}")
    public ResponseEntity<Void> delete(@PathVariable Long kilnId, @PathVariable Long firingId) {
        firingService.delete(kilnId, firingId);
        return ResponseEntity.noContent().build();
    }
}
