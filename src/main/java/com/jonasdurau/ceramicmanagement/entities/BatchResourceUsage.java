package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_batch_resource_usage")
public class BatchResourceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double initialQuantity;
    private double umidity;
    private double addedQuantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    public BatchResourceUsage() {
    }

    public BatchResourceUsage(Long id, double initialQuantity, double umidity, double addedQuantity, Batch batch, Resource resource) {
        this.id = id;
        this.initialQuantity = initialQuantity;
        this.umidity = umidity;
        this.addedQuantity = addedQuantity;
        this.batch = batch;
        this.resource = resource;
    }

    public double getTotalQuantity() {
        return initialQuantity + addedQuantity;
    }

    public double getTotalWater() {
        return umidity * getTotalQuantity();
    }

    public BigDecimal getTotalCost() {
        return resource.getUnitValue().multiply(BigDecimal.valueOf(getTotalQuantity()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public double getUmidity() {
        return umidity;
    }

    public void setUmidity(double umidity) {
        this.umidity = umidity;
    }

    public double getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(double addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
        BatchResourceUsage other = (BatchResourceUsage) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
