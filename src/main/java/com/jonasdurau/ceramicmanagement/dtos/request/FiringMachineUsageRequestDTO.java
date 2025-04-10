package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.Positive;

public record FiringMachineUsageRequestDTO(
    @Positive(message = "O tempo de uso da máquina deve ser positivo.")
    Double usageTime,
    @Positive(message = "O Id da máquina deve ser positivo.")
    long machineId
) {}
