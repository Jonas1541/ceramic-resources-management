package com.jonasdurau.ceramicmanagement.dtos;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlazeTransactionRequestDTO {

    @Positive(message = "A quantidade deve ser positiva.")
    private double quantity;

    @NotNull(message = "O tipo de transação é obrigatório.")
    private TransactionType type;

    public GlazeTransactionRequestDTO() {
    }

    public double getQuantity() {
        return quantity;
    }

    public TransactionType getType() {
        return type;
    }
}
