package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

public record ProductTransactionResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    Instant outgoingAt,
    ProductState state,
    ProductOutgoingReason outgoingReason,
    String productName,
    Long bisqueFiringId,
    Long glazeFiringId,
    String glazeColor,
    double glazeQuantity,
    BigDecimal cost,
    BigDecimal profit
) {}
