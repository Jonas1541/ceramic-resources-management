package com.jonasdurau.ceramicmanagement.dtos.response;

public record EmployeeUsageResponseDTO(
    Long employeeId,
    String employeeName,
    double usageTime
) {}
