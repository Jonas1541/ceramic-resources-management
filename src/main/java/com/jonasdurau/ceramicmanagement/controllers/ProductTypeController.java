package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.ProductTypeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.ProductTypeResponseDTO;
import com.jonasdurau.ceramicmanagement.services.ProductTypeService;

@RestController
@RequestMapping("/api/product-types")
public class ProductTypeController extends IndependentController<ProductTypeResponseDTO, ProductTypeRequestDTO, ProductTypeResponseDTO, Long, ProductTypeService>{
}
