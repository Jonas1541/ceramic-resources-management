package com.jonasdurau.ceramicmanagement.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_kiln")
public class Kiln extends BaseEntity {

    private String name;

    @ManyToMany
    @JoinTable(
        name = "tb_kiln_machine",
        joinColumns = @JoinColumn(name = "kiln_id"),
        inverseJoinColumns = @JoinColumn(name = "machine_id")
    )
    private List<Machine> machines = new ArrayList<>();

    @OneToMany(mappedBy = "kiln")
    private List<BisqueFiring> bisqueFirings = new ArrayList<>();

    @OneToMany(mappedBy = "kiln")
    private List<GlazeFiring> glazeFirings = new ArrayList<>();

    public Kiln() {
    }

    public double getPower() {
        double totalPower = 0.0;
        for (Machine m : machines) {
            totalPower += m.getPower();
        }
        return totalPower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public List<BisqueFiring> getBisqueFirings() {
        return bisqueFirings;
    }

    public List<GlazeFiring> getGlazeFirings() {
        return glazeFirings;
    }
}
