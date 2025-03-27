package com.jonasdurau.ceramicmanagement.dtos.list;

import java.math.BigDecimal;
import java.time.Instant;

public class GlazeListDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String color;
    private BigDecimal unitValue;
    private BigDecimal unitCost;
    private double currentQuantity;
    private BigDecimal currentQuantityPrice;

    public GlazeListDTO() {
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getCurrentQuantityPrice() {
        return currentQuantityPrice;
    }

    public void setCurrentQuantityPrice(BigDecimal currentQuantityPrice) {
        this.currentQuantityPrice = currentQuantityPrice;
    }
}
