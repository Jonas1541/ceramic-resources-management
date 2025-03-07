package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GlazeFiringResponseDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    private double temperature;

    private double burnTime;

    private double coolingTime;

    private double gasConsumption;

    private String kilnName;

    private List<GlostResponseDTO> glosts = new ArrayList<>();

    private List<FiringMachineUsageResponseDTO> machineUsages = new ArrayList<>();

    private BigDecimal cost;

    public GlazeFiringResponseDTO() {
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(double burnTime) {
        this.burnTime = burnTime;
    }

    public double getCoolingTime() {
        return coolingTime;
    }

    public void setCoolingTime(double coolingTime) {
        this.coolingTime = coolingTime;
    }

    public double getGasConsumption() {
        return gasConsumption;
    }

    public void setGasConsumption(double gasConsumption) {
        this.gasConsumption = gasConsumption;
    }

    public String getKilnName() {
        return kilnName;
    }

    public void setKilnName(String kilnName) {
        this.kilnName = kilnName;
    }

    public List<GlostResponseDTO> getGlosts() {
        return glosts;
    }

    public List<FiringMachineUsageResponseDTO> getMachineUsages() {
        return machineUsages;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
