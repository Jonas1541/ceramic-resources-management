package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.ResourceTransactionDTO;
import com.jonasdurau.ceramicmanagement.services.ResourceTransactionService;

@RestController
@RequestMapping("/resources/{parentId}/transactions")
public class ResourceTransactionController extends DependentController<ResourceTransactionDTO, ResourceTransactionDTO, ResourceTransactionDTO, Long, ResourceTransactionService>{
}
