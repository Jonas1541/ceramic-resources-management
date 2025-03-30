package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.ProductLineDTO;
import com.jonasdurau.ceramicmanagement.services.ProductLineService;

@RestController
@RequestMapping("/product-lines")
public class ProductLineController extends IndependentController<ProductLineDTO, ProductLineDTO, ProductLineDTO, Long, ProductLineService>{
}
