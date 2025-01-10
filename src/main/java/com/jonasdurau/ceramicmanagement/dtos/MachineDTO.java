package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class MachineDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero")
    private double power;

    public MachineDTO() {
    }

    public MachineDTO(Long id, String name, double power) {
        this.id = id;
        this.name = name;
        this.power = power;
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

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
