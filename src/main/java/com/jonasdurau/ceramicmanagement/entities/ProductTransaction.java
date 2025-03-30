package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_product_transaction")
public class ProductTransaction extends BaseEntity {

    private Instant outgoingAt;

    @Enumerated(EnumType.STRING)
    private ProductState state;

    @Enumerated(EnumType.STRING)
    private ProductOutgoingReason outgoingReason;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(optional = true, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "glaze_transaction_id")
    private GlazeTransaction glazeTransaction;

    @ManyToOne(optional = true)
    @JoinColumn(name = "bisque_firing_id")
    private BisqueFiring bisqueFiring;

    @ManyToOne(optional = true)
    @JoinColumn(name = "glaze_firing_id")
    private GlazeFiring glazeFiring;

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
}
