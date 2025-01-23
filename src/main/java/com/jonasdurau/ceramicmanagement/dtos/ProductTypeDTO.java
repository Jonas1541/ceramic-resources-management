package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public class ProductTypeDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotBlank(message = "O nome é obrigatório")
    private String name;
    private int productQuantity;

    public ProductTypeDTO() {
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

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
