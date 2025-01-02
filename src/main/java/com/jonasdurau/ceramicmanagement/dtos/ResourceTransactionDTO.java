package com.jonasdurau.ceramicmanagement.dtos;

import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ResourceTransactionDTO {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull(message = "O tipo de transação é obrigatório")
    private TransactionType type;

    @Min(value = 0, message = "A quantidade deve ser positiva")
    private double quantity;

    private Long resourceId;

    private String cost;

    public ResourceTransactionDTO() {
    }

    public ResourceTransactionDTO(Long id, Instant createdAt, Instant updatedAt, TransactionType type, double quantity, Long resourceId, String cost) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.quantity = quantity;
        this.resourceId = resourceId;
        this.cost = cost;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
