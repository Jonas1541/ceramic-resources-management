package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ProductRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductResponseDTO;
import com.jonasdurau.ceramicmanagement.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController extends IndependentController<ProductResponseDTO, ProductRequestDTO, ProductResponseDTO, Long, ProductService> {

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> list = service.yearlyReport(id);
        return ResponseEntity.ok(list);
    }
}
