package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.services.GeneralReportService;

@RestController
@RequestMapping("/api/general-report")
public class GeneralReportController {
    
    @Autowired
    private GeneralReportService service;

    @GetMapping
    public ResponseEntity<List<YearReportDTO>> generalYearlyReport() {
        List<YearReportDTO> report = service.generalYearlyReport();
        return ResponseEntity.ok(report);
    }
}
