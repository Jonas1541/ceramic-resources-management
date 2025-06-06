package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.request.CompanyRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.CompanyResponseDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DeletionStatusResponseDTO;
import com.jonasdurau.ceramicmanagement.services.CompanyService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> registerCompany(@Valid @RequestBody CompanyRequestDTO dto) throws IOException {
        CompanyResponseDTO company = companyService.registerCompany(dto);
        return ResponseEntity.ok(company);
    }

    @PatchMapping("/me/schedule-deletion")
    public ResponseEntity<Void> scheduleOwnAccountDeletion() {
        companyService.scheduleCurrentCompanyDeletion();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/cancel-deletion")
    public ResponseEntity<Void> cancelOwnAccountDeletion() {
        companyService.cancelCurrentCompanyDeletion();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/deletion-status")
    public ResponseEntity<DeletionStatusResponseDTO> getOwnDeletionStatus() {
        return ResponseEntity.ok(companyService.getCurrentCompanyDeletionStatus());
    }
}
