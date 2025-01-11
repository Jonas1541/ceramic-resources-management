package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_batch")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchResourceUsage> resourceUsages = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchMachineUsage> machineUsages = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceTransaction> resourceTransactions = new ArrayList<>();

    private BigDecimal batchTotalWaterCostAtTime;
    private BigDecimal resourceTotalCostAtTime;
    private BigDecimal machinesEnergyConsumptionCostAtTime;
    private BigDecimal batchFinalCostAtTime;

    public Batch() {
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

    public double getBatchTotalWater() {
        return resourceUsages.stream()
                .mapToDouble(BatchResourceUsage::getTotalWater)
                .sum();
    }

    public double getResourceTotalQuantity() {
        return resourceUsages.stream()
                .mapToDouble(BatchResourceUsage::getTotalQuantity)
                .sum();
    }

    public BigDecimal getResourceTotalCost() {
        return resourceUsages.stream()
                .map(BatchResourceUsage::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double getMachinesEnergyConsumption() {
        return machineUsages.stream()
                .mapToDouble(BatchMachineUsage::getEnergyConsumption)
                .sum();
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

    public List<BatchResourceUsage> getResourceUsages() {
        return resourceUsages;
    }

    public List<BatchMachineUsage> getMachineUsages() {
        return machineUsages;
    }

    public List<ResourceTransaction> getResourceTransactions() {
        return resourceTransactions;
    }

    public BigDecimal getBatchTotalWaterCostAtTime() {
        return batchTotalWaterCostAtTime;
    }

    public void setBatchTotalWaterCostAtTime(BigDecimal batchTotalWaterCostAtTime) {
        this.batchTotalWaterCostAtTime = batchTotalWaterCostAtTime;
    }

    public BigDecimal getResourceTotalCostAtTime() {
        return resourceTotalCostAtTime;
    }

    public void setResourceTotalCostAtTime(BigDecimal resourceTotalCostAtTime) {
        this.resourceTotalCostAtTime = resourceTotalCostAtTime;
    }

    public BigDecimal getMachinesEnergyConsumptionCostAtTime() {
        return machinesEnergyConsumptionCostAtTime;
    }

    public void setMachinesEnergyConsumptionCostAtTime(BigDecimal machinesEnergyConsumptionCostAtTime) {
        this.machinesEnergyConsumptionCostAtTime = machinesEnergyConsumptionCostAtTime;
    }

    public BigDecimal getBatchFinalCostAtTime() {
        return batchFinalCostAtTime;
    }

    public void setBatchFinalCostAtTime(BigDecimal batchFinalCostAtTime) {
        this.batchFinalCostAtTime = batchFinalCostAtTime;
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
        Batch other = (Batch) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
