package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class KilnDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @Positive(message = "A potência deve ser positiva.")
    private double power;

    private int totalBisqueFirings;
    private BigDecimal totalBisqueFiringsCost;
    private int totalGlazeFirings;
    private BigDecimal totalGlazeFiringsCost;

    public KilnDTO() {
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

    public int getTotalBisqueFirings() {
        return totalBisqueFirings;
    }

    public void setTotalBisqueFirings(int totalBisqueFirings) {
        this.totalBisqueFirings = totalBisqueFirings;
    }

    public BigDecimal getTotalBisqueFiringsCost() {
        return totalBisqueFiringsCost;
    }

    public void setTotalBisqueFiringsCost(BigDecimal totalBisqueFiringsCost) {
        this.totalBisqueFiringsCost = totalBisqueFiringsCost;
    }

    public int getTotalGlazeFirings() {
        return totalGlazeFirings;
    }

    public void setTotalGlazeFirings(int totalGlazeFirings) {
        this.totalGlazeFirings = totalGlazeFirings;
    }

    public BigDecimal getTotalGlazeFiringsCost() {
        return totalGlazeFiringsCost;
    }

    public void setTotalGlazeFiringsCost(BigDecimal totalGlazeFiringsCost) {
        this.totalGlazeFiringsCost = totalGlazeFiringsCost;
    }
}
