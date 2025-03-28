package com.jonasdurau.ceramicmanagement.dtos.list;

import java.math.BigDecimal;

public record ResourceListDTO(
    Long id,
    String name,
    String category,
    BigDecimal unitValue,
    double currentQuantity,
    BigDecimal currentQuantityPrice
) {}

