package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlazeMachineUsageDTO {
    
    @NotNull(message = "O ID da máquina não pode ser nulo.")
    @Positive(message = "O ID da máquina deve ser positivo.")
    private long machineId;

    @Positive(message = "O tempo de uso deve ser maior que 0.")
    private double usageTime;

    public GlazeMachineUsageDTO() {
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public double getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(double usageTime) {
        this.usageTime = usageTime;
    }
}
