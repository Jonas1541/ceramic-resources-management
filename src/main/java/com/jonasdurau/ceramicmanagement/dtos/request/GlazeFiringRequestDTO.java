package com.jonasdurau.ceramicmanagement.dtos.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record GlazeFiringRequestDTO(
    @Positive(message = "A temperatura deve ser positiva")
    double temperature,
    @Positive(message = "O tempo de queima deve ser positivo")
    double burnTime,
    @Positive(message = "O tempo de resfriamento deve ser positivo")
    double coolingTime,
    @Positive(message = "O consumo de gás deve ser positivo")
    double gasConsumption,
    @NotEmpty(message = "A queima deve ter produtos")
    @Valid
    List<GlostRequestDTO> glosts,
    @Valid
    List<FiringMachineUsageRequestDTO> machineUsages
) {
    public GlazeFiringRequestDTO {
        if (machineUsages == null) {
            machineUsages = new ArrayList<>();
        }
    }
}
