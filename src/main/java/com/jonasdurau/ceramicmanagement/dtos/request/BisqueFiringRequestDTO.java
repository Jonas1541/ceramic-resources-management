package com.jonasdurau.ceramicmanagement.dtos.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record BisqueFiringRequestDTO(
    @Positive(message = "A temperatura deve ser positiva")
    double temperature,
    @Positive(message = "O tempo de queima deve ser positivo")
    double burnTime,
    @PositiveOrZero(message = "O tempo de resfriamento deve ser maior ou igual a zero")
    double coolingTime,
    @Positive(message = "O consumo de gás deve ser positivo")
    double gasConsumption,
    @NotEmpty(message = "A queima deve ter produtos")
    List<@Positive(message = "O id dos produtos devem ser positivos") Long> biscuits,
    @Valid
    List<FiringMachineUsageRequestDTO> machineUsages
) {
    public BisqueFiringRequestDTO {
        if (machineUsages == null) {
            machineUsages = new ArrayList<>();
        }
    }
}
