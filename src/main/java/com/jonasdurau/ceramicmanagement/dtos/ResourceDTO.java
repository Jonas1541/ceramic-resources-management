package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ResourceDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String name;

    @NotNull(message = "A categoria é obrigatória")
    private ResourceCategory category;

    @NotNull(message = "O valor não pode ser nulo.")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal unitValue;

    private double currentQuantity;
    private BigDecimal currentQuantityPrice;

    public ResourceDTO() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
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
