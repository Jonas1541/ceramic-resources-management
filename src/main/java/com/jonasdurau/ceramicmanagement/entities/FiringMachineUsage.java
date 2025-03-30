package com.jonasdurau.ceramicmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_firing_machine_usage")
public class FiringMachineUsage extends BaseEntity {
    
    private double usageTime;

    @ManyToOne(optional = true)
    @JoinColumn(name = "bisque_firing_id")
    private BisqueFiring bisqueFiring;

    @ManyToOne(optional = true)
    @JoinColumn(name = "glaze_firing_id")
    private GlazeFiring glazeFiring;

    @ManyToOne(optional = false)
    @JoinColumn(name = "machine_id")
    private Machine machine;

    public FiringMachineUsage() {
    }

    public double getEnergyConsumption() {
        double power = machine.getPower() * 0.74;
        return power * usageTime;
    }

    public double getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(double usageTime) {
        this.usageTime = usageTime;
    }

    public BisqueFiring getBisqueFiring() {
        return bisqueFiring;
    }

    public void setBisqueFiring(BisqueFiring bisqueFiring) {
        this.bisqueFiring = bisqueFiring;
    }

    public GlazeFiring getGlazeFiring() {
        return glazeFiring;
    }

    public void setGlazeFiring(GlazeFiring glazeFiring) {
        this.glazeFiring = glazeFiring;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }
}
