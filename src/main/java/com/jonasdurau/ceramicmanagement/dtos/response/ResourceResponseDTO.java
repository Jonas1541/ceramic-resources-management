package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;

public record ResourceResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    ResourceCategory category,
    BigDecimal unitValue,
    double currentQuantity,
    BigDecimal currentQuantityPrice
) {}
