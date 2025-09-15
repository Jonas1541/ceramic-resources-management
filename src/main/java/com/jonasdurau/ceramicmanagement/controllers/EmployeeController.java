package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.EmployeeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.EmployeeResponseDTO;
import com.jonasdurau.ceramicmanagement.services.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController extends IndependentController<EmployeeResponseDTO, EmployeeRequestDTO, EmployeeResponseDTO, Long, EmployeeService>{
}
