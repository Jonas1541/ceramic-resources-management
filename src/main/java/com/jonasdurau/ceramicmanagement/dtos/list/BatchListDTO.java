package com.jonasdurau.ceramicmanagement.dtos.list;

import java.math.BigDecimal;
import java.time.Instant;

public class BatchListDTO {

    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private BigDecimal batchFinalCost;

    public BatchListDTO() {
    }

    public BatchListDTO(Long id, Instant createdAt, Instant updatedAt, BigDecimal batchFinalCost) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.batchFinalCost = batchFinalCost;
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

    public BigDecimal getBatchFinalCost() {
        return batchFinalCost;
    }

    public void setBatchFinalCost(BigDecimal batchFinalCost) {
        this.batchFinalCost = batchFinalCost;
    }
}
