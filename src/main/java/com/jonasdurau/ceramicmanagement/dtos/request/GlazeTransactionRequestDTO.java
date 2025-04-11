package com.jonasdurau.ceramicmanagement.dtos.request;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GlazeTransactionRequestDTO(
    @Positive(message = "A quantidade deve ser positiva.")
    double quantity,
    @NotNull(message = "O tipo de transação é obrigatório.")
    TransactionType type
) {}
