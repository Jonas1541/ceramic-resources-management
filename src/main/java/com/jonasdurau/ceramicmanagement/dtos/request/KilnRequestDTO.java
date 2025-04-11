package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record KilnRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    @Positive(message = "A potência deve ser positiva.")
    double power
) {}
