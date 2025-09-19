package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DryingSessionResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    double hours,
    List<EmployeeUsageResponseDTO> employeeUsages,
    BigDecimal employeeTotalCost,
    BigDecimal costAtTime
) {}
