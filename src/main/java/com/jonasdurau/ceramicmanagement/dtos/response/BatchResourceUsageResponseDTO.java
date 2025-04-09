package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;

public record BatchResourceUsageResponseDTO(
    Long resourceId,
    String name,
    double initialQuantity,
    double umidity,
    double addedQuantity,
    double totalQuantity,
    double totalWater,
    BigDecimal totalCost
) {}
