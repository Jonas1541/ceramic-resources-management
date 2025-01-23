package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_product_transaction")
public class ProductTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant outgoingAt;

    @Enumerated(EnumType.STRING)
    private ProductState state;

    @Enumerated(EnumType.STRING)
    private ProductOutgoingReason outgoingReason;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "glaze_transaction_id")
    private GlazeTransaction glazeTransaction;

    public ProductTransaction() {
    }

    public BigDecimal getProfit() {
        BigDecimal profit;
        if(outgoingReason == ProductOutgoingReason.SOLD) {
            profit = product.getPrice();
        } else {
            profit = BigDecimal.valueOf(0);
        }
        return profit;
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

    public Instant getOutgoingAt() {
        return outgoingAt;
    }

    public void setOutgoingAt(Instant outgoingAt) {
        this.outgoingAt = outgoingAt;
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public ProductOutgoingReason getOutgoingReason() {
        return outgoingReason;
    }

    public void setOutgoingReason(ProductOutgoingReason outgoingReason) {
        this.outgoingReason = outgoingReason;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public GlazeTransaction getGlazeTransaction() {
        return glazeTransaction;
    }

    public void setGlazeTransaction(GlazeTransaction glazeTransaction) {
        this.glazeTransaction = glazeTransaction;
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
        ProductTransaction other = (ProductTransaction) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
