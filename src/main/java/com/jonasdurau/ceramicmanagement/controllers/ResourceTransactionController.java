package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.ResourceTransactionDTO;
import com.jonasdurau.ceramicmanagement.services.ResourceTransactionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources/{resourceId}/transactions")
public class ResourceTransactionController {

    @Autowired
    private ResourceTransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<ResourceTransactionDTO>> findAllByResource(@PathVariable Long resourceId) {
        List<ResourceTransactionDTO> list = transactionService.findAllByResource(resourceId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ResourceTransactionDTO> findById(@PathVariable Long resourceId, @PathVariable Long transactionId) {
        ResourceTransactionDTO dto = transactionService.findById(resourceId, transactionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ResourceTransactionDTO> create(@PathVariable Long resourceId, @Valid @RequestBody ResourceTransactionDTO dto) {
        ResourceTransactionDTO created = transactionService.create(resourceId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<ResourceTransactionDTO> update(@PathVariable Long resourceId, @PathVariable Long transactionId, @Valid @RequestBody ResourceTransactionDTO dto) {
        ResourceTransactionDTO updated = transactionService.update(resourceId, transactionId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long resourceId, @PathVariable Long transactionId) {
        transactionService.delete(resourceId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
