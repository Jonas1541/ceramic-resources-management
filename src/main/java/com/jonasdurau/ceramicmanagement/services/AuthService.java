package com.jonasdurau.ceramicmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonasdurau.ceramicmanagement.config.TenantContext;
import com.jonasdurau.ceramicmanagement.config.TokenService;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.InvalidCredentialsException;
import com.jonasdurau.ceramicmanagement.dtos.LoginDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.TokenResponseDTO;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;

@Service
public class AuthService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TokenResponseDTO login(LoginDTO dto) {
        // Busca a empresa pelo email
        Company company = companyRepository.findByEmail(dto.email())
            .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        // Verifica a senha (já deve estar criptografada)
        if (!passwordEncoder.matches(dto.password(), company.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        // Configura o TenantContext com a URL e porta do banco
        TenantContext.setCurrentTenant(company.getDatabaseUrl() + ":" + company.getDatabasePort());

        // Gera o token JWT contendo os dados do tenant
        String token = tokenService.generateToken(company);

        return new TokenResponseDTO(token);
    }
}
