package com.jonasdurau.ceramicmanagement.dtos.list;

import java.time.Instant;

public record DryingRoomListDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    double gasConsumptionPerHour
) {}
