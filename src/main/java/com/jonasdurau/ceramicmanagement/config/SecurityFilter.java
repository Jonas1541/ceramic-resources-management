package com.jonasdurau.ceramicmanagement.config;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ExpiredTokenException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.InvalidTokenException;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recoverToken(request);
        try {
            if (token != null) {
                String email = tokenService.getEmailFromToken(token);
                Company company = companyRepository.findByEmail(email).orElse(null);

                if (company != null) {
                    // Configura o TenantContext com o identificador do tenant
                    TenantContext.setCurrentTenant("company_" + company.getName().toLowerCase().replace(" ", "_"));

                    var authentication = new UsernamePasswordAuthenticationToken(company, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredTokenException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token Expired\",\"message\":\"" + ex.getMessage() + "\"}");
        } catch (InvalidTokenException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token\",\"message\":\"" + ex.getMessage() + "\"}");
        } finally {
            // Limpa o TenantContext após a requisição
            TenantContext.clear();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}
