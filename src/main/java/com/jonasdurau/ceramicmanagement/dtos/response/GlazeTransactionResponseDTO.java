package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

public class GlazeTransactionResponseDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private double quantity;
    private TransactionType type;
    private String glazeColor;
    private BigDecimal resourceTotalCostAtTime;
    private BigDecimal machineEnergyConsumptionCostAtTime;
    private BigDecimal glazeFinalCostAtTime;

    public GlazeTransactionResponseDTO() {
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getGlazeColor() {
        return glazeColor;
    }

    public void setGlazeColor(String glazeColor) {
        this.glazeColor = glazeColor;
    }

    public BigDecimal getResourceTotalCostAtTime() {
        return resourceTotalCostAtTime;
    }

    public void setResourceTotalCostAtTime(BigDecimal resourceTotalCostAtTime) {
        this.resourceTotalCostAtTime = resourceTotalCostAtTime;
    }

    public BigDecimal getMachineEnergyConsumptionCostAtTime() {
        return machineEnergyConsumptionCostAtTime;
    }

    public void setMachineEnergyConsumptionCostAtTime(BigDecimal machineEnergyConsumptionCostAtTime) {
        this.machineEnergyConsumptionCostAtTime = machineEnergyConsumptionCostAtTime;
    }

    public BigDecimal getGlazeFinalCostAtTime() {
        return glazeFinalCostAtTime;
    }

    public void setGlazeFinalCostAtTime(BigDecimal glazeFinalCostAtTime) {
        this.glazeFinalCostAtTime = glazeFinalCostAtTime;
    }
}
