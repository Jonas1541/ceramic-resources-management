package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.Min;

public class BatchMachineUsageDTO {
    
    @Min(value = 1, message = "O ID da máquina deve ser positivo.")
    private Long machineId;

    @Min(value = 1, message = "O tempo de uso deve ser maior que 0.")
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
