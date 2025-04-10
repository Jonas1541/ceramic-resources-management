package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MachineRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    @Positive(message = "O valor deve ser maior que zero")
    double power
) {}
