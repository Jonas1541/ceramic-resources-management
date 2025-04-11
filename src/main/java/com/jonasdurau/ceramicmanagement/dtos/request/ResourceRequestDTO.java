package com.jonasdurau.ceramicmanagement.dtos.request;

import java.math.BigDecimal;

import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ResourceRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    @NotNull(message = "A categoria é obrigatória")
    ResourceCategory category,
    @NotNull(message = "O valor não pode ser nulo.")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal unitValue
) {}
