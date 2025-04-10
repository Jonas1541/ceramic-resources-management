package com.jonasdurau.ceramicmanagement.dtos.response;

public record GlazeMachineUsageResponseDTO(
    long machineId,
    String machineName,
    double usageTime
) {}
