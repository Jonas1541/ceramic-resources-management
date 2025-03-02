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

import com.jonasdurau.ceramicmanagement.dtos.GlazeDTO;
import com.jonasdurau.ceramicmanagement.dtos.GlazeListDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/glazes")
public class GlazeController {

    @Autowired
    private GlazeService glazeService;

    @GetMapping
    public ResponseEntity<List<GlazeListDTO>> findAll() {
        List<GlazeListDTO> list = glazeService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlazeDTO> findById(@PathVariable Long id) {
        GlazeDTO dto = glazeService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<GlazeDTO> create(@Valid @RequestBody GlazeDTO dto) {
        GlazeDTO created = glazeService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlazeDTO> update(@PathVariable Long id, @Valid @RequestBody GlazeDTO dto) {
        GlazeDTO updated = glazeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        glazeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> report = glazeService.yearlyReport(id);
        return ResponseEntity.ok(report);
    }
}
