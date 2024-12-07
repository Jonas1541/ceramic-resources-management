package com.jonasdurau.ceramicmanagement.repositories;

import com.jonasdurau.ceramicmanagement.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Método para buscar uma empresa por email
    Optional<Company> findByEmail(String email);

    // Método para verificar se um CNPJ já está registrado
    boolean existsByCnpj(String cnpj);

    // Método para verificar se um email já está registrado
    boolean existsByEmail(String email);
}
