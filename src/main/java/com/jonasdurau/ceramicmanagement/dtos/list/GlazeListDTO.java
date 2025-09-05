package com.jonasdurau.ceramicmanagement.dtos.list;

import java.math.BigDecimal;
import java.time.Instant;

public record GlazeListDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String color,
    BigDecimal unitCost,
    double currentQuantity,
    BigDecimal currentQuantityPrice
) {}