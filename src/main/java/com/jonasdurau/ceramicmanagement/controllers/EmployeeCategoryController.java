package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.EmployeeCategoryRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.EmployeeCategoryResponseDTO;
import com.jonasdurau.ceramicmanagement.services.EmployeeCategoryService;

@RestController
@RequestMapping("/api/employee-categories")
public class EmployeeCategoryController extends IndependentController<EmployeeCategoryResponseDTO, EmployeeCategoryRequestDTO, EmployeeCategoryResponseDTO, Long, EmployeeCategoryService>{
}
