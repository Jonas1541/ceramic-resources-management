package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.Positive;

public class FiringMachineUsageRequestDTO {

    @Positive(message = "O tempo de uso da máquina deve ser positivo.")
    private Double usageTime;

    @Positive(message = "O Id da máquina deve ser positivo.")
    private long machineId;

    public FiringMachineUsageRequestDTO() {
    }

    public Double getUsageTime() {
        return usageTime;
    }

    public long getMachineId() {
        return machineId;
    }
}
