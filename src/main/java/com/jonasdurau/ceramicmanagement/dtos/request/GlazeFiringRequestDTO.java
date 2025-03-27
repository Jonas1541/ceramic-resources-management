package com.jonasdurau.ceramicmanagement.dtos.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class GlazeFiringRequestDTO {

    @Positive(message = "A temperatura deve ser positiva")
    private double temperature;

    @Positive(message = "O tempo de queima deve ser positivo")
    private double burnTime;

    @Positive(message = "O tempo de resfriamento deve ser positivo")
    private double coolingTime;

    @Positive(message = "O consumo de gás deve ser positivo")
    private double gasConsumption;

    @NotEmpty(message = "A queima deve ter produtos")
    @Valid
    private List<GlostRequestDTO> glosts = new ArrayList<>();

    @Valid
    private List<FiringMachineUsageRequestDTO> machineUsages = new ArrayList<>();

    public GlazeFiringRequestDTO() {
    }

    public double getTemperature() {
        return temperature;
    }

    public double getBurnTime() {
        return burnTime;
    }

    public double getCoolingTime() {
        return coolingTime;
    }

    public double getGasConsumption() {
        return gasConsumption;
    }

    public List<GlostRequestDTO> getGlosts() {
        return glosts;
    }

    public List<FiringMachineUsageRequestDTO> getMachineUsages() {
        return machineUsages;
    }
}
