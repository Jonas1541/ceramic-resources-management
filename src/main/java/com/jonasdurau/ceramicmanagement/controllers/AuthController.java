package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.LoginDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ForgotPasswordRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.ResetPasswordRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.TokenResponseDTO;
import com.jonasdurau.ceramicmanagement.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        TokenResponseDTO tokenDTO = authService.login(dto);
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
