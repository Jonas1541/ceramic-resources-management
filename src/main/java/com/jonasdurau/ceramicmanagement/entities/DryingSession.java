package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_drying_session")
public class DryingSession extends BaseEntity {

    private double hours;

    @ManyToOne(optional = false)
    @JoinColumn(name = "drying_room_id")
    private DryingRoom dryingRoom;

    private BigDecimal costAtTime;

    public DryingSession() {
    }

    public double getEnergyConsumption() {
        double totalKW = dryingRoom.getMachines().stream().mapToDouble(Machine::getPower).sum() * 0.74;
        return totalKW * hours;
    }

    public double getGasConsumption() {
        return dryingRoom.getGasConsumptionPerHour() * hours;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public DryingRoom getDryingRoom() {
        return dryingRoom;
    }

    public void setDryingRoom(DryingRoom dryingRoom) {
        this.dryingRoom = dryingRoom;
    }

    public BigDecimal getCostAtTime() {
        return costAtTime;
    }

    public void setCostAtTime(BigDecimal costAtTime) {
        this.costAtTime = costAtTime;
    }
}
