package com.jonasdurau.ceramicmanagement.dtos.response;

public record BatchEmployeeUsageResponseDTO(
    Long employeeId,
    double usageTime
) {}
