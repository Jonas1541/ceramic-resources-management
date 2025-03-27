package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlostRequestDTO {
    
    @NotNull(message = "O id do produto deve ser informado")
    @Positive(message = "O id da transação do produto deve ser positivo")
    private Long productTransactionId;

    @Positive(message = "O id da glasura deve ser positivo")
    private Long glazeId;

    @Positive(message = "A quantidade de glasura deve ser positiva")
    private Double quantity;

    public GlostRequestDTO() {
    }

    public Long getProductTransactionId() {
        return productTransactionId;
    }

    public Long getGlazeId() {
        return glazeId;
    }

    public Double getQuantity() {
        return quantity;
    }
}
