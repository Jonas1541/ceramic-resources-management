package com.jonasdurau.ceramicmanagement.repositories;

import com.jonasdurau.ceramicmanagement.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmail(String email);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
