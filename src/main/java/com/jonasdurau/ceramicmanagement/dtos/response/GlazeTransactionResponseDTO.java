package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

public record GlazeTransactionResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    double quantity,
    TransactionType type,
    String glazeColor,
    BigDecimal resourceTotalCostAtTime,
    BigDecimal machineEnergyConsumptionCostAtTime,
    BigDecimal glazeFinalCostAtTime
) {}
