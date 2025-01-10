package com.jonasdurau.ceramicmanagement.dtos;

public class BatchMachineUsageDTO {
    
    private Long machineId;
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
