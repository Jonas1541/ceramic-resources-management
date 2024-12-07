package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.dtos.LoginDTO;
import com.jonasdurau.ceramicmanagement.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginDTO dto) {
        String token = authService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(token);
    }
}
