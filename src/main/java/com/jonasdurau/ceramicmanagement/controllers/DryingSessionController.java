package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.DryingSessionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.DryingSessionResponseDTO;
import com.jonasdurau.ceramicmanagement.services.DryingSessionService;

@RestController
@RequestMapping("/api/drying-rooms/{parentId}/drying-sessions")
public class DryingSessionController extends DependentController<DryingSessionResponseDTO, DryingSessionRequestDTO, DryingSessionResponseDTO, Long, DryingSessionService> {
}
