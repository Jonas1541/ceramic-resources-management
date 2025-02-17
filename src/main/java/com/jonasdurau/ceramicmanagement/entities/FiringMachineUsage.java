package com.jonasdurau.ceramicmanagement.entities;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_firing_machine_usage")
public class FiringMachineUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
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
        FiringMachineUsage other = (FiringMachineUsage) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
