package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;

public class ResourceListDTO {
    
    private Long id;
    private String name;
    private String category;
    private BigDecimal unitValue;
    private double currentQuantity;
    private BigDecimal currentQuantityPrice;

    public ResourceListDTO() {
    }

    public ResourceListDTO(Long id, String name, String category, BigDecimal unitValue, double currentQuantity, BigDecimal currentQuantityPrice) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitValue = unitValue;
        this.currentQuantity = currentQuantity;
        this.currentQuantityPrice = currentQuantityPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
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

