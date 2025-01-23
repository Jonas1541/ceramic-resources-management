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

import com.jonasdurau.ceramicmanagement.dtos.ProductLineDTO;
import com.jonasdurau.ceramicmanagement.services.ProductLineService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product-lines")
public class ProductLineController {
    
    @Autowired
    private ProductLineService productLineService;

    @GetMapping
    public ResponseEntity<List<ProductLineDTO>> findAll() {
        List<ProductLineDTO> list = productLineService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductLineDTO> findById(@PathVariable Long id) {
        ProductLineDTO dto = productLineService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductLineDTO> create(@Valid @RequestBody ProductLineDTO dto) {
        ProductLineDTO created = productLineService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductLineDTO> update(@PathVariable Long id, @Valid @RequestBody ProductLineDTO dto) {
        ProductLineDTO updated = productLineService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productLineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
