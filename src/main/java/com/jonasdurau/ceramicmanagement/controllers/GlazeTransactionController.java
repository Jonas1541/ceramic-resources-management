package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.GlazeTransactionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeTransactionResponseDTO;
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
    public ResponseEntity<List<GlazeTransactionResponseDTO>> findAllByGlaze(@PathVariable Long glazeId) {
        List<GlazeTransactionResponseDTO> list = transactionService.findAllByGlaze(glazeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<GlazeTransactionResponseDTO> findById(@PathVariable Long glazeId, @PathVariable Long transactionId) {
        GlazeTransactionResponseDTO dto = transactionService.findById(glazeId, transactionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GlazeTransactionResponseDTO> create(@PathVariable Long glazeId, @Valid @RequestBody GlazeTransactionRequestDTO dto) {
        GlazeTransactionResponseDTO created = transactionService.create(glazeId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<GlazeTransactionResponseDTO> update(@PathVariable Long glazeId, @PathVariable Long transactionId, @Valid @RequestBody GlazeTransactionRequestDTO dto) {
        GlazeTransactionResponseDTO updated = transactionService.update(glazeId, transactionId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long glazeId, @PathVariable Long transactionId) {
        transactionService.delete(glazeId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
