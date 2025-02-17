package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;

import jakarta.validation.constraints.Positive;

public class FiringMachineUsageDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @Positive(message = "O tempo de uso da máquina deve ser positivo.")
    private Double usageTime;

    private Long bisqueFiringId;

    private Long glazeFiringId;

    @Positive(message = "O Id da máquina deve ser positivo.")
    private long machineId;

    public FiringMachineUsageDTO() {
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

    public Long getBisqueFiringId() {
        return bisqueFiringId;
    }

    public void setBisqueFiringId(Long bisqueFiringId) {
        this.bisqueFiringId = bisqueFiringId;
    }

    public Long getGlazeFiringId() {
        return glazeFiringId;
    }

    public void setGlazeFiringId(Long glazeFiringId) {
        this.glazeFiringId = glazeFiringId;
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }
}
