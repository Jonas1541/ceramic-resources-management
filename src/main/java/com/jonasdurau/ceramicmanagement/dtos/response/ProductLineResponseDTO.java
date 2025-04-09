package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record ProductLineResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    int productQuantity
) {}
