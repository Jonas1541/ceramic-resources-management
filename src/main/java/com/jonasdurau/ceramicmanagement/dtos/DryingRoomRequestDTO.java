package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class DryingRoomRequestDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @Positive(message = "O consumo de gás por hora deve ser positivo")
    private double gasConsumptionPerHour;

    private List<@Positive(message = "O id das máquinas devem ser positivos") Long> machines = new ArrayList<>();

    public DryingRoomRequestDTO() {
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

    public double getGasConsumptionPerHour() {
        return gasConsumptionPerHour;
    }

    public void setGasConsumptionPerHour(double gasConsumptionPerHour) {
        this.gasConsumptionPerHour = gasConsumptionPerHour;
    }

    public List<Long> getMachines() {
        return machines;
    }

    public void setMachines(List<Long> machines) {
        this.machines = machines;
    }
}
