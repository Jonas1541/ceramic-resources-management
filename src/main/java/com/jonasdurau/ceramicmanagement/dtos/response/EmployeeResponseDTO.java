package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

public record EmployeeResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    long categoryId,
    String categoryName,
    BigDecimal costPerHour
) {}
