package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_glaze")
public class Glaze extends BaseEntity {
    
    private String color;
    private BigDecimal unitValue;

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeResourceUsage> resourceUsages = new ArrayList<>();

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeMachineUsage> machineUsages = new ArrayList<>();

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeTransaction> transactions = new ArrayList<>();

    private BigDecimal unitCost;

    public Glaze() {
    }

    public double getCurrentQuantity() {
        double total = 0.0;
        for (GlazeTransaction tx : transactions) {
            if(tx.getType() == TransactionType.INCOMING) {
                total += tx.getQuantity();
            }
            else {
                total -= tx.getQuantity();
            }
        }
        return total;
    }

    public BigDecimal getCurrentQuantityPrice() {
        return BigDecimal.valueOf(getCurrentQuantity())
                .multiply(this.unitValue)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    public List<GlazeResourceUsage> getResourceUsages() {
        return resourceUsages;
    }

    public List<GlazeMachineUsage> getMachineUsages() {
        return machineUsages;
    }

    public List<GlazeTransaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }
}
