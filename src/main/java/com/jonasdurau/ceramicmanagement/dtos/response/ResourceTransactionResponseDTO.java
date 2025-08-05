package com.jonasdurau.ceramicmanagement.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

public record ResourceTransactionResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    TransactionType type,
    double quantity,
    String resourceName,
    Long batchId,
    Long glazeTxId,
    BigDecimal cost
) {}
