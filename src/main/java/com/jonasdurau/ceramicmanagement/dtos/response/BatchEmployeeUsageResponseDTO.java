package com.jonasdurau.ceramicmanagement.dtos.response;

public record BatchEmployeeUsageResponseDTO(
    Long employeeId,
    String employeeName,
    double usageTime
) {}
