package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_glaze_transaction")
public class GlazeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private double quantity;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "glaze_id")
    private Glaze glaze;

    private BigDecimal resourceTotalCostAtTime;
    private BigDecimal machineEnergyConsumptionCostAtTime;
    private BigDecimal glazeFinalCostAtTime;

    @OneToMany(mappedBy = "glazeTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceTransaction> resourceTransactions = new ArrayList<>();

    public GlazeTransaction() {
    }

    public BigDecimal getCost() {
        return glaze.getUnitValue()
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Glaze getGlaze() {
        return glaze;
    }

    public void setGlaze(Glaze glaze) {
        this.glaze = glaze;
    }

    public BigDecimal getResourceTotalCostAtTime() {
        return resourceTotalCostAtTime;
    }

    public void setResourceTotalCostAtTime(BigDecimal resourceTotalCostAtTime) {
        this.resourceTotalCostAtTime = resourceTotalCostAtTime;
    }

    public BigDecimal getMachineEnergyConsumptionCostAtTime() {
        return machineEnergyConsumptionCostAtTime;
    }

    public void setMachineEnergyConsumptionCostAtTime(BigDecimal machineEnergyConsumptionCostAtTime) {
        this.machineEnergyConsumptionCostAtTime = machineEnergyConsumptionCostAtTime;
    }

    public BigDecimal getGlazeFinalCostAtTime() {
        return glazeFinalCostAtTime;
    }

    public void setGlazeFinalCostAtTime(BigDecimal glazeFinalCostAtTime) {
        this.glazeFinalCostAtTime = glazeFinalCostAtTime;
    }

    public List<ResourceTransaction> getResourceTransactions() {
        return resourceTransactions;
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
        GlazeTransaction other = (GlazeTransaction) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
