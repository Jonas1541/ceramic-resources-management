package com.jonasdurau.ceramicmanagement.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_kiln")
public class Kiln extends BaseEntity {

    private String name;
    private double power;

    @OneToMany(mappedBy = "kiln")
    private List<BisqueFiring> bisqueFirings = new ArrayList<>();

    @OneToMany(mappedBy = "kiln")
    private List<GlazeFiring> glazeFirings = new ArrayList<>();

    public Kiln() {
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
}
