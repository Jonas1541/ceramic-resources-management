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
@Table(name = "tb_bisque_firing")
public class BisqueFiring extends BaseEntity {

    private double temperature;
    private double burnTime;
    private double coolingTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "kiln_id")
    private Kiln kiln;

    @OneToMany(mappedBy = "bisqueFiring")
    private List<ProductTransaction> biscuits = new ArrayList<>();

    private BigDecimal costAtTime;

    public BisqueFiring() {
    }

    public double getEnergyConsumption() {
        return (kiln.getPower() * 0.74) * (burnTime + coolingTime);
    }

    public double getGasConsumption() {
        return burnTime * kiln.getGasConsumptionPerHour();
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

    public Kiln getKiln() {
        return kiln;
    }

    public void setKiln(Kiln kiln) {
        this.kiln = kiln;
    }

    public List<ProductTransaction> getBiscuits() {
        return biscuits;
    }

    public BigDecimal getCostAtTime() {
        return costAtTime;
    }

    public void setCostAtTime(BigDecimal costAtTime) {
        this.costAtTime = costAtTime;
    }
}
