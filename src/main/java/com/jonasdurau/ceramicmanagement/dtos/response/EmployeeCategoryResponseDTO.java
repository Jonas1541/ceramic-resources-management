package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record EmployeeCategoryResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name
) {}
