package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record KilnResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    double power
) {}
