package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_resource_transaction")
public class ResourceTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private double quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne(optional = true)
    @JoinColumn(name = "batch_id", nullable = true)
    private Batch batch;

    private BigDecimal costAtTime;

    public ResourceTransaction() {
    }

    public BigDecimal getCost() {
        return resource.getUnitValue()
                       .multiply(BigDecimal.valueOf(quantity))
                       .setScale(2, RoundingMode.HALF_UP);
        }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
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

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public BigDecimal getCostAtTime() {
        return costAtTime;
    }

    public void setCostAtTime(BigDecimal costAtTime) {
        this.costAtTime = costAtTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceTransaction other = (ResourceTransaction) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
