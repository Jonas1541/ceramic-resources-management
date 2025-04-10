package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record MachineResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    double power
) {}
