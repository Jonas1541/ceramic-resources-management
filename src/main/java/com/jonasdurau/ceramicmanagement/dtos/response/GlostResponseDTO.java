package com.jonasdurau.ceramicmanagement.dtos.response;

public record GlostResponseDTO(
    String productName,
    String glazeColor,
    Double quantity
) {}
