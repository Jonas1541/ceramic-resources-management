package com.jonasdurau.ceramicmanagement.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record GlazeRequestDTO(
    @NotBlank(message = "a cor é obrigatória")
    String color,
    @NotEmpty(message = "A lista de recursos não pode estar vazia.")
    @Valid
    List<GlazeResourceUsageRequestDTO> resourceUsages,
    @NotEmpty(message = "A lista de máquinas não pode estar vazia.")
    @Valid
    List<GlazeMachineUsageRequestDTO> machineUsages,
    @NotEmpty(message = "A lista de funcionários não pode estar vazia.")
    @Valid
    List<EmployeeUsageRequestDTO> employeeUsages
) {}
