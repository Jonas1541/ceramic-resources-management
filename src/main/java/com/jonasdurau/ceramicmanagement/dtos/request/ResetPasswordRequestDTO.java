package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
    @NotBlank String token,
    @NotBlank String password
) {
}
