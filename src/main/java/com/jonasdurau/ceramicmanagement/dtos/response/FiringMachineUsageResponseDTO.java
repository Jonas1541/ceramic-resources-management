package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record FiringMachineUsageResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    Double usageTime,
    String machineName
) {}
