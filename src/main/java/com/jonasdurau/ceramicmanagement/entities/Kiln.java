package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_kiln")
public class Kiln {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private double power;

    @OneToMany(mappedBy = "kiln")
    private List<BisqueFiring> bisqueFirings = new ArrayList<>();

    @OneToMany(mappedBy = "kiln")
    private List<GlazeFiring> glazeFirings = new ArrayList<>();

    public Kiln() {
    }

    public int getTotalBisqueFirings() {
        return bisqueFirings.size();
    }

    public int getTotalGlazeFirings() {
        return glazeFirings.size();
    }

    public BigDecimal getTotalBisqueFiringsCost() {
        return bisqueFirings.stream().map(BisqueFiring::getCostAtTime).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalGlazeFiringsCost() {
        return glazeFirings.stream().map(GlazeFiring::getCostAtTime).reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public List<BisqueFiring> getBisqueFirings() {
        return bisqueFirings;
    }

    public List<GlazeFiring> getGlazeFirings() {
        return glazeFirings;
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
        Kiln other = (Kiln) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
