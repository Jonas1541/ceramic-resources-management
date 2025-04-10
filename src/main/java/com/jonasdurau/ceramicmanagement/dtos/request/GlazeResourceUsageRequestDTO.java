package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.Positive;

public record GlazeResourceUsageRequestDTO(
    @Positive(message = "O ID do recurso deve ser positivo.")
    Long resourceId,
    @Positive(message = "A quantidade deve ser positiva.")
    double quantity
) {}
