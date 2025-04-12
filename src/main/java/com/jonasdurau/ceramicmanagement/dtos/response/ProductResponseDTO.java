package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    BigDecimal price,
    double height,
    double length,
    double width,
    String type,
    String line,
    int productStock
) {}
