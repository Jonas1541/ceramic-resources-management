package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.GlazeTransactionDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeTransactionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/glazes/{glazeId}/transactions")
public class GlazeTransactionController {

    @Autowired
    private GlazeTransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<GlazeTransactionDTO>> findAllByGlaze(@PathVariable Long glazeId) {
        List<GlazeTransactionDTO> list = transactionService.findAllByGlaze(glazeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<GlazeTransactionDTO> findById(@PathVariable Long glazeId, @PathVariable Long transactionId) {
        GlazeTransactionDTO dto = transactionService.findById(glazeId, transactionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GlazeTransactionDTO> create(@PathVariable Long glazeId, @Valid @RequestBody GlazeTransactionDTO dto) {
        GlazeTransactionDTO created = transactionService.create(glazeId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<GlazeTransactionDTO> update(@PathVariable Long glazeId, @PathVariable Long transactionId, @Valid @RequestBody GlazeTransactionDTO dto) {
        GlazeTransactionDTO updated = transactionService.update(glazeId, transactionId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long glazeId, @PathVariable Long transactionId) {
        transactionService.delete(glazeId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
