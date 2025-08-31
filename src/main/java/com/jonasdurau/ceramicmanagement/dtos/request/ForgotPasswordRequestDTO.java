package com.jonasdurau.ceramicmanagement.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
    @NotBlank @Email String email
) {
}
