package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class BatchDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotEmpty(message = "A lista de recursos não pode estar vazia.")
    @Valid
    private List<BatchResourceUsageDTO> resourceUsages = new ArrayList<>();

    @NotEmpty(message = "A lista de máquinas não pode estar vazia.")
    @Valid
    private List<BatchMachineUsageDTO> machineUsages = new ArrayList<>();

    private double batchTotalWater;
    private BigDecimal batchTotalWaterCost;
    private double resourceTotalQuantity;
    private BigDecimal resourceTotalCost;
    private double machinesEnergyConsumption;
    private BigDecimal machinesEnergyConsumptionCost;
    private BigDecimal batchFinalCost;

    public BatchDTO() {
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

    public List<BatchResourceUsageDTO> getResourceUsages() {
        return resourceUsages;
    }

    public void setResourceUsages(List<BatchResourceUsageDTO> resourceUsages) {
        this.resourceUsages = resourceUsages;
    }

    public List<BatchMachineUsageDTO> getMachineUsages() {
        return machineUsages;
    }

    public void setMachineUsages(List<BatchMachineUsageDTO> machineUsages) {
        this.machineUsages = machineUsages;
    }

    public double getBatchTotalWater() {
        return batchTotalWater;
    }

    public void setBatchTotalWater(double batchTotalWater) {
        this.batchTotalWater = batchTotalWater;
    }

    public BigDecimal getBatchTotalWaterCost() {
        return batchTotalWaterCost;
    }

    public void setBatchTotalWaterCost(BigDecimal batchTotalWaterCost) {
        this.batchTotalWaterCost = batchTotalWaterCost;
    }

    public double getResourceTotalQuantity() {
        return resourceTotalQuantity;
    }

    public void setResourceTotalQuantity(double resourceTotalQuantity) {
        this.resourceTotalQuantity = resourceTotalQuantity;
    }

    public BigDecimal getResourceTotalCost() {
        return resourceTotalCost;
    }

    public void setResourceTotalCost(BigDecimal resourceTotalCost) {
        this.resourceTotalCost = resourceTotalCost;
    }

    public double getMachinesEnergyConsumption() {
        return machinesEnergyConsumption;
    }

    public void setMachinesEnergyConsumption(double machinesEnergyConsumption) {
        this.machinesEnergyConsumption = machinesEnergyConsumption;
    }

    public BigDecimal getMachinesEnergyConsumptionCost() {
        return machinesEnergyConsumptionCost;
    }

    public void setMachinesEnergyConsumptionCost(BigDecimal machinesEnergyConsumptionCost) {
        this.machinesEnergyConsumptionCost = machinesEnergyConsumptionCost;
    }

    public BigDecimal getBatchFinalCost() {
        return batchFinalCost;
    }

    public void setBatchFinalCost(BigDecimal batchFinalCost) {
        this.batchFinalCost = batchFinalCost;
    }
}
