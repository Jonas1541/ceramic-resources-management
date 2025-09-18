package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BatchResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    List<BatchResourceUsageResponseDTO> resourceUsages,
    List<BatchMachineUsageResponseDTO> machineUsages,
    List<EmployeeUsageResponseDTO> employeeUsages,
    double batchTotalWater,
    BigDecimal batchTotalWaterCost,
    double resourceTotalQuantity,
    BigDecimal resourceTotalCost,
    double machinesEnergyConsumption,
    BigDecimal machinesEnergyConsumptionCost,
    BigDecimal employeeTotalCost,
    BigDecimal batchFinalCost,
    double weight
) {}
