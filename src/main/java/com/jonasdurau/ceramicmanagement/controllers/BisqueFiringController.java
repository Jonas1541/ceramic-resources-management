package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BisqueFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BisqueFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.services.BisqueFiringService;

@RestController
@RequestMapping("/api/kilns/{parentId}/bisque-firings")
public class BisqueFiringController extends DependentController<FiringListDTO, BisqueFiringRequestDTO, BisqueFiringResponseDTO, Long, BisqueFiringService> {
}
