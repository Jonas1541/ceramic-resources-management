package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlazeDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotBlank(message = "a cor é obrigatória")
    private String color;

    @NotNull(message = "O valor não pode ser nulo.")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal unitValue;

    @NotEmpty(message = "A lista de recursos não pode estar vazia.")
    @Valid
    private List<GlazeResourceUsageDTO> resourceUsages = new ArrayList<>();

    @NotEmpty(message = "A lista de máquinas não pode estar vazia.")
    @Valid
    private List<GlazeMachineUsageDTO> machineUsages = new ArrayList<>();

    private BigDecimal unitCost;

    public GlazeDTO() {
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

    public List<GlazeResourceUsageDTO> getResourceUsages() {
        return resourceUsages;
    }

    public List<GlazeMachineUsageDTO> getMachineUsages() {
        return machineUsages;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
