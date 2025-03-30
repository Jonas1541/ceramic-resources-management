package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.DryingSessionDTO;
import com.jonasdurau.ceramicmanagement.services.DryingSessionService;

@RestController
@RequestMapping("/drying-rooms/{parentId}/drying-sessions")
public class DryingSessionController extends DependentController<DryingSessionDTO, DryingSessionDTO, DryingSessionDTO, Long, DryingSessionService> {
}
