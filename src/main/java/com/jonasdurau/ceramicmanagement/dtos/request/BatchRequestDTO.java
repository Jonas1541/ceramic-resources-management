package com.jonasdurau.ceramicmanagement.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record BatchRequestDTO(
    @NotEmpty(message = "A lista de recursos não pode estar vazia.")
    @Valid
    List<BatchResourceUsageRequestDTO> resourceUsages,
    @NotEmpty(message = "A lista de máquinas não pode estar vazia.")
    @Valid
    List<BatchMachineUsageRequestDTO> machineUsages,
    @NotEmpty(message = "A lista de funcionários não pode estar vazia.")
    @Valid
    List<BatchEmployeeUsageRequestDTO> employeeUsages
) {}
