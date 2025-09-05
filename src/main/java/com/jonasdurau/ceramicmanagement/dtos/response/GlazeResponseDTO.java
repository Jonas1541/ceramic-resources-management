package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record GlazeResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String color,
    List<GlazeResourceUsageResponseDTO> resourceUsages,
    List<GlazeMachineUsageResponseDTO> machineUsages,
    BigDecimal unitCost,
    double currentQuantity,
    BigDecimal currentQuantityPrice
) {
    public GlazeResponseDTO {
        resourceUsages = resourceUsages == null ? new ArrayList<>() : resourceUsages;
        machineUsages = machineUsages == null ? new ArrayList<>() : machineUsages;
    }
}
