package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;

public record EmployeeUsageResponseDTO(
    Long employeeId,
    String employeeName,
    double usageTime,
    BigDecimal employeeCost
) {}
