package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.DryingSessionDTO;
import com.jonasdurau.ceramicmanagement.services.DryingSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/drying-rooms/{roomId}/drying-sessions")
public class DryingSessionController {
    
    @Autowired
    private DryingSessionService service;

    @GetMapping
    public ResponseEntity<List<DryingSessionDTO>> findAllByRoom(@PathVariable Long roomId) {
        List<DryingSessionDTO> list = service.findAllByRoom(roomId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<DryingSessionDTO> findById(@PathVariable Long roomId, @PathVariable Long sessionId) {
        DryingSessionDTO dto = service.findById(roomId, sessionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<DryingSessionDTO> create(@PathVariable Long roomId, @Valid @RequestBody DryingSessionDTO dto) {
        DryingSessionDTO created = service.create(roomId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<DryingSessionDTO> update(@PathVariable Long roomId, @PathVariable Long sessionId, @Valid @RequestBody DryingSessionDTO dto) {
        DryingSessionDTO updated = service.update(roomId, sessionId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable Long roomId, @PathVariable Long sessionId) {
        service.delete(roomId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
