package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.dtos.CompanyDTO;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Company registerCompany(CompanyDTO dto) throws IOException {
        // 1. Criptografa a senha
        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        // 2. Cria o banco da empresa
        String databaseName = "company_" + dto.getName().toLowerCase().replace(" ", "_");
        databaseService.createDatabase(databaseName);

        // Carrega o schema.sql do classpath
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            throw new RuntimeException("schema.sql not found in resources");
        }
        databaseService.initializeSchema(databaseName, schemaStream);

        // 3. Adiciona o novo banco de dados ao DynamicDataSource
        String databaseUrl = "jdbc:mysql://localhost/" + databaseName;
        databaseService.addTenant(databaseName, databaseUrl, 3306, "root", "root");

        // 4. Salva a empresa no banco principal
        Company company = new Company();
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setCnpj(dto.getCnpj());
        company.setPassword(hashedPassword);
        company.setDatabaseUrl(databaseUrl);
        company.setDatabasePort(3306);
        company.setDatabaseName(databaseName); // Agora salvamos também o databaseName
        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());

        return companyRepository.save(company);
    }
}
