package com.jonasdurau.ceramicmanagement.dtos.list;

import java.math.BigDecimal;
import java.time.Instant;

public record FiringListDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    double temperature,
    double burnTime,
    double coolingTime,
    double gasConsumption,
    String kilnName,
    BigDecimal cost
) {}
