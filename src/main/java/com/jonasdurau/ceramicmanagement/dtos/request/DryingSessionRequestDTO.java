package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.Positive;

public record DryingSessionRequestDTO(
    @Positive(message = "A quantidade de horas de uso deve ser positiva")
    double hours
) {}
