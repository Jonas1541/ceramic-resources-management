package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;

public class FiringMachineUsageResponseDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    private Double usageTime;

    private String machineName;

    public FiringMachineUsageResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(Double usageTime) {
        this.usageTime = usageTime;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
