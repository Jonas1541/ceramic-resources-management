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

import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.DryingRoomListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.DryingRoomRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DryingRoomResponseDTO;
import com.jonasdurau.ceramicmanagement.services.DryingRoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/drying-rooms")
public class DryingRoomController {
    
    @Autowired
    private DryingRoomService service;

    @GetMapping
    public ResponseEntity<List<DryingRoomListDTO>> findAll() {
        List<DryingRoomListDTO> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DryingRoomResponseDTO> findById(@PathVariable Long id) {
        DryingRoomResponseDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<DryingRoomResponseDTO> create(@Valid @RequestBody DryingRoomRequestDTO dto) {
        DryingRoomResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DryingRoomResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DryingRoomRequestDTO dto) {
        DryingRoomResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> report = service.yearlyReport(id);
        return ResponseEntity.ok(report);
    }
}
