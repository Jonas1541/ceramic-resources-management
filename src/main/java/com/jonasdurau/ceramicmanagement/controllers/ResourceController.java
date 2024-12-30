package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.ResourceDTO;
import com.jonasdurau.ceramicmanagement.services.ResourceService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping
    public ResponseEntity<List<ResourceDTO>> findAll() {
        List<ResourceDTO> list = resourceService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> findById(@PathVariable Long id) {
        ResourceDTO dto = resourceService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ResourceDTO> create(@Valid @RequestBody ResourceDTO dto) {
        ResourceDTO created = resourceService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id, @Valid @RequestBody ResourceDTO dto) {
        ResourceDTO updated = resourceService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
