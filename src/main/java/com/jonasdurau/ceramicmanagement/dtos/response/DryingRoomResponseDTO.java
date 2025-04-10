package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record DryingRoomResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    double gasConsumptionPerHour,
    List<MachineResponseDTO> machines
) {
    public DryingRoomResponseDTO {
        if (machines == null) {
            machines = new ArrayList<>();
        }
    }
}
