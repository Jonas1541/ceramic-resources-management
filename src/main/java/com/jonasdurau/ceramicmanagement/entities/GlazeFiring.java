package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_glaze_firing")
public class GlazeFiring extends BaseEntity {
    
    private double temperature;
    private double burnTime;
    private double coolingTime;
    private double gasConsumption;

    @ManyToOne(optional = false)
    @JoinColumn(name = "kiln_id")
    private Kiln kiln;

    @OneToMany(mappedBy = "glazeFiring")
    private List<ProductTransaction> glosts = new ArrayList<>();

    private BigDecimal costAtTime;

    public GlazeFiring() {
    }

    public double getEnergyConsumption() {
        return (kiln.getPower() * 0.74) * (burnTime + coolingTime);
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(double burnTime) {
        this.burnTime = burnTime;
    }

    public double getCoolingTime() {
        return coolingTime;
    }

    public void setCoolingTime(double coolingTime) {
        this.coolingTime = coolingTime;
    }

    public double getGasConsumption() {
        return gasConsumption;
    }

    public void setGasConsumption(double gasConsumption) {
        this.gasConsumption = gasConsumption;
    }

    public Kiln getKiln() {
        return kiln;
    }

    public void setKiln(Kiln kiln) {
        this.kiln = kiln;
    }

    public List<ProductTransaction> getGlosts() {
        return glosts;
    }

    public BigDecimal getCostAtTime() {
        return costAtTime;
    }

    public void setCostAtTime(BigDecimal costAtTime) {
        this.costAtTime = costAtTime;
    }
}
