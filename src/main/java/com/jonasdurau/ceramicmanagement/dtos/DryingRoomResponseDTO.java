package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DryingRoomResponseDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    private String name;

    private double gasConsumptionPerHour;

    private List<MachineDTO> machines = new ArrayList<>();

    public DryingRoomResponseDTO() {
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

    public List<MachineDTO> getMachines() {
        return machines;
    }

    public void setMachines(List<MachineDTO> machines) {
        this.machines = machines;
    }
}
