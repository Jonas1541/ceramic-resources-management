package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record KilnResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    double power,
    double gasConsumptionPerHour,
    List<MachineResponseDTO> machines
) {
    public KilnResponseDTO {
        if (machines == null) {
            machines = new ArrayList<>();
        }
    }
}