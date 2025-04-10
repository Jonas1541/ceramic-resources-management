package com.jonasdurau.ceramicmanagement.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GlazeRequestDTO(
    @NotBlank(message = "a cor é obrigatória")
    String color,
    @NotNull(message = "O valor não pode ser nulo.")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal unitValue,
    @NotEmpty(message = "A lista de recursos não pode estar vazia.")
    @Valid
    List<GlazeResourceUsageRequestDTO> resourceUsages,
    @NotEmpty(message = "A lista de máquinas não pode estar vazia.")
    @Valid
    List<GlazeMachineUsageRequestDTO> machineUsages
) {}
