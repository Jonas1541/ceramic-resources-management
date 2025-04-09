package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

public record DryingSessionResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    double hours,
    BigDecimal costAtTime
) {}
