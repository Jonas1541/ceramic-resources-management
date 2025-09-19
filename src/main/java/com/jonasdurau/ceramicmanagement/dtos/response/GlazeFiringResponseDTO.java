package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record GlazeFiringResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    double temperature,
    double burnTime,
    double coolingTime,
    double gasConsumption,
    String kilnName,
    List<GlostResponseDTO> glosts,
    List<EmployeeUsageResponseDTO> employeeUsages,
    BigDecimal employeeTotalCost,
    BigDecimal cost
) {}
