package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record CompanyResponseDTO(
    Long id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    String email,
    String cnpj,
    String password
) {}
