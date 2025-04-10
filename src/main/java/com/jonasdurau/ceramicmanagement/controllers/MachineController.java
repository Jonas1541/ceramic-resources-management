package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.request.MachineRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.MachineResponseDTO;
import com.jonasdurau.ceramicmanagement.services.MachineService;

@RestController
@RequestMapping("/machines")
public class MachineController extends IndependentController<MachineResponseDTO, MachineRequestDTO, MachineResponseDTO, Long, MachineService>{
}
