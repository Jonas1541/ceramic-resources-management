package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

public class ProductTransactionResponseDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant outgoingAt;

    private ProductState state;

    private ProductOutgoingReason outgoingReason;

    private String productName;

    private String glazeColor;

    private double glazeQuantity;

    private BigDecimal profit;

    public ProductTransactionResponseDTO() {
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGlazeColor() {
        return glazeColor;
    }

    public void setGlazeColor(String glazeColor) {
        this.glazeColor = glazeColor;
    }

    public double getGlazeQuantity() {
        return glazeQuantity;
    }

    public void setGlazeQuantity(double glazeQuantity) {
        this.glazeQuantity = glazeQuantity;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}
