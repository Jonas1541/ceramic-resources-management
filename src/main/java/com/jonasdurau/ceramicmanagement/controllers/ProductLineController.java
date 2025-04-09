package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.ProductLineRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductLineResponseDTO;
import com.jonasdurau.ceramicmanagement.services.ProductLineService;

@RestController
@RequestMapping("/product-lines")
public class ProductLineController extends IndependentController<ProductLineResponseDTO, ProductLineRequestDTO, ProductLineResponseDTO, Long, ProductLineService>{
}
