package com.jonasdurau.ceramicmanagement.dtos.response;

public record GlazeResourceUsageResponseDTO(
    Long resourceId,
    String resourceName,
    double quantity
) {}
