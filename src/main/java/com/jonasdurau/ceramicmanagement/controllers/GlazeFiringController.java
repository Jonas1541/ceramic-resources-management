package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeFiringService;

@RestController
@RequestMapping("/kilns/{parentId}/glaze-firings")
public class GlazeFiringController extends DependentController<FiringListDTO, GlazeFiringRequestDTO, GlazeFiringResponseDTO, Long, GlazeFiringService> {
}
