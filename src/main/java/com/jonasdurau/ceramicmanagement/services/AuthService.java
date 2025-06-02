package com.jonasdurau.ceramicmanagement.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.config.TenantContext;
import com.jonasdurau.ceramicmanagement.config.TokenService;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.main.CompanyRepository;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.InvalidCredentialsException;
import com.jonasdurau.ceramicmanagement.dtos.LoginDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.TokenResponseDTO;

@Service
public class AuthService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(transactionManager = "tenantTransactionManager")
    public TokenResponseDTO login(LoginDTO dto) {
        TenantContext.clear(); 
        Company company = companyRepository.findByEmail(dto.email())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));
        if (!passwordEncoder.matches(dto.password(), company.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
        company.setLastActivityAt(Instant.now());
        companyRepository.save(company);
        TenantContext.setCurrentTenant(company.getDatabaseName());
        String token = tokenService.generateToken(company);
        return new TokenResponseDTO(token);
    }
}