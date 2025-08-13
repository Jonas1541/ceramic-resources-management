package com.jonasdurau.ceramicmanagement.dtos.response;

public record GlostResponseDTO(
    Long productTxId,
    String productName,
    String glazeColor,
    Double quantity
) {}
