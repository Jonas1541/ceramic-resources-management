package com.jonasdurau.ceramicmanagement.dtos.response;

public record BatchMachineUsageResponseDTO(
    Long machineId,
    String name,
    double usageTime,
    double energyConsumption
) {}
