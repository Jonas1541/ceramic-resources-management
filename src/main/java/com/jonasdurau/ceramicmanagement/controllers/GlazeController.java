package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.GlazeDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.GlazeListDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeService;

@RestController
@RequestMapping("/glazes")
public class GlazeController extends IndependentController<GlazeListDTO, GlazeDTO, GlazeDTO, Long, GlazeService> {

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> report = service.yearlyReport(id);
        return ResponseEntity.ok(report);
    }
}
