package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.ResourceListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ResourceRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ResourceResponseDTO;
import com.jonasdurau.ceramicmanagement.services.ResourceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController extends IndependentController<ResourceListDTO, ResourceRequestDTO, ResourceResponseDTO, Long, ResourceService> {

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> report = service.yearlyReport(id);
        return ResponseEntity.ok(report);
    }
}
