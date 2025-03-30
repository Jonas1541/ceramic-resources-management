package com.jonasdurau.ceramicmanagement.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.MachineDTO;
import com.jonasdurau.ceramicmanagement.services.MachineService;

@RestController
@RequestMapping("/machines")
public class MachineController extends IndependentController<MachineDTO, MachineDTO, MachineDTO, Long, MachineService>{
}
