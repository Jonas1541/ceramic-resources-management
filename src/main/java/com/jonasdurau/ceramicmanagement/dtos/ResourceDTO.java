package com.jonasdurau.ceramicmanagement.dtos;

import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResourceDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String name;

    @NotNull(message = "A categoria é obrigatória")
    private ResourceCategory category;

    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private double unitValue;

    public ResourceDTO() {
    }

    public ResourceDTO(Long id, String name, ResourceCategory category, double unitValue) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitValue = unitValue;
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

    public ResourceCategory getCategory() {
        return category;
    }

    public double getUnitValue() {
        return unitValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(ResourceCategory category) {
        this.category = category;
    }

    public void setUnitValue(double unitValue) {
        this.unitValue = unitValue;
    }

}
