package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BatchMachineUsageDTO {
    
    @NotNull(message = "O ID da máquina não pode ser nulo.")
    @Positive(message = "O ID da máquina deve ser positivo.")
    private Long machineId;

    @Positive(message = "O tempo de uso deve ser maior que 0.")
    private long usageTimeSeconds;

    private double energyConsumption; 

    public BatchMachineUsageDTO() {
    }

    public BatchMachineUsageDTO(Long machineId, long usageTimeSeconds, double energyConsumption) {
        this.machineId = machineId;
        this.usageTimeSeconds = usageTimeSeconds;
        this.energyConsumption = energyConsumption;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public long getUsageTimeSeconds() {
        return usageTimeSeconds;
    }

    public void setUsageTimeSeconds(long usageTimeSeconds) {
        this.usageTimeSeconds = usageTimeSeconds;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
}
