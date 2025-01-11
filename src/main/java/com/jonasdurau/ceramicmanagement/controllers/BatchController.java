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

import com.jonasdurau.ceramicmanagement.dtos.BatchDTO;
import com.jonasdurau.ceramicmanagement.dtos.BatchListDTO;
import com.jonasdurau.ceramicmanagement.services.BatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchListDTO>> findAll() {
        List<BatchListDTO> list = batchService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchDTO> findById(@PathVariable Long id) {
        BatchDTO dto = batchService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BatchDTO> create(@Valid @RequestBody BatchDTO dto) {
        BatchDTO created = batchService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchDTO> update(@PathVariable Long id, @Valid @RequestBody BatchDTO dto) {
        BatchDTO updated = batchService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        batchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

