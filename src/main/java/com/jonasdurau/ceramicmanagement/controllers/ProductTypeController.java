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

import com.jonasdurau.ceramicmanagement.dtos.ProductTypeDTO;
import com.jonasdurau.ceramicmanagement.services.ProductTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product-types")
public class ProductTypeController {
    
    @Autowired
    private ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<List<ProductTypeDTO>> findAll() {
        List<ProductTypeDTO> list = productTypeService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> findById(@PathVariable Long id) {
        ProductTypeDTO dto = productTypeService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductTypeDTO> create(@Valid @RequestBody ProductTypeDTO dto) {
        ProductTypeDTO created = productTypeService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductTypeDTO> update(@PathVariable Long id, @Valid @RequestBody ProductTypeDTO dto) {
        ProductTypeDTO updated = productTypeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
