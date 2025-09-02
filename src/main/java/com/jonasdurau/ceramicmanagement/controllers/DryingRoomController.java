package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.list.DryingRoomListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.DryingRoomRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DryingRoomResponseDTO;
import com.jonasdurau.ceramicmanagement.services.DryingRoomService;

@RestController
@RequestMapping("/api/drying-rooms")
public class DryingRoomController extends IndependentController<DryingRoomListDTO, DryingRoomRequestDTO, DryingRoomResponseDTO, Long, DryingRoomService> {

    @GetMapping("/{id}/yearly-report")
    public ResponseEntity<List<YearReportDTO>> yearlyReport(@PathVariable Long id) {
        List<YearReportDTO> report = service.yearlyReport(id);
        return ResponseEntity.ok(report);
    }
}
