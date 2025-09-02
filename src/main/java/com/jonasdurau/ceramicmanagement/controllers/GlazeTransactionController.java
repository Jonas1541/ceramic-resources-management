package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.GlazeTransactionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.services.GlazeTransactionService;

@RestController
@RequestMapping("/api/glazes/{parentId}/transactions")
public class GlazeTransactionController extends DependentController<GlazeTransactionResponseDTO, GlazeTransactionRequestDTO, GlazeTransactionResponseDTO, Long, GlazeTransactionService> {
}
