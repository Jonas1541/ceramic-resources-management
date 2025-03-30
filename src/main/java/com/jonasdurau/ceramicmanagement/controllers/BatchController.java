package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.BatchDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.BatchListDTO;
import com.jonasdurau.ceramicmanagement.services.BatchService;

@RestController
@RequestMapping("/batches")
public class BatchController extends IndependentController<BatchListDTO, BatchDTO, BatchDTO, Long, BatchService>{

    @GetMapping("/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport() {
        List<YearReportDTO> report = service.yearlyReport();
        return ResponseEntity.ok(report);
    }
}
