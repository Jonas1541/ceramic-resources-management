package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record ProductTypeRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name
) {}
