package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

public class ProductTransactionDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant outgoingAt;

    private ProductState state;

    private ProductOutgoingReason outgoingReason;

    private long productId;

    private Long glazeTransactionId;

    private BigDecimal profit;

    public ProductTransactionDTO() {
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

    public Instant getOutgoingAt() {
        return outgoingAt;
    }

    public void setOutgoingAt(Instant outgoingAt) {
        this.outgoingAt = outgoingAt;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public ProductOutgoingReason getOutgoingReason() {
        return outgoingReason;
    }

    public void setOutgoingReason(ProductOutgoingReason outgoingReason) {
        this.outgoingReason = outgoingReason;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public Long getGlazeTransactionId() {
        return glazeTransactionId;
    }

    public void setGlazeTransactionId(Long glazeTransactionId) {
        this.glazeTransactionId = glazeTransactionId;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}
