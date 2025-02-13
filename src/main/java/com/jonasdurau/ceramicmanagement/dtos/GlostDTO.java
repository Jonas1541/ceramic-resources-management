package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlostDTO {
    
    @NotNull(message = "O id do produto deve ser informado")
    @Positive(message = "O id da transação do produto deve ser positivo")
    private Long productTransactionId;

    @Positive(message = "O id da glasura deve ser positivo")
    private Long glazeId;

    @Positive(message = "A quantidade de glasura deve ser positiva")
    private Double quantity;

    public GlostDTO() {
    }

    public Long getProductTransactionId() {
        return productTransactionId;
    }

    public void setProductTransactionId(Long productTransactionId) {
        this.productTransactionId = productTransactionId;
    }

    public Long getGlazeId() {
        return glazeId;
    }

    public void setGlazeId(Long glazeId) {
        this.glazeId = glazeId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
