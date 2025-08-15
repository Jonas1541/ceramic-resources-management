package com.jonasdurau.ceramicmanagement.dtos.response;

public record GlostResponseDTO(
    Long productId,
    Long productTxId,
    String productName,
    String glazeColor,
    Double quantity
) {}
