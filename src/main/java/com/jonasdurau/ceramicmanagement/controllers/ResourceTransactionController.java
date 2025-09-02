package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.ResourceTransactionRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ResourceTransactionResponseDTO;
import com.jonasdurau.ceramicmanagement.services.ResourceTransactionService;

@RestController
@RequestMapping("/api/resources/{parentId}/transactions")
public class ResourceTransactionController extends DependentController<ResourceTransactionResponseDTO, ResourceTransactionRequestDTO, ResourceTransactionResponseDTO, Long, ResourceTransactionService>{
}
