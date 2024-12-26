package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.config.DynamicDataSource;
import com.jonasdurau.ceramicmanagement.dtos.CompanyDTO;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource; // Usaremos para verificar o DynamicDataSource

    public Company registerCompany(CompanyDTO dto) throws IOException {

        // 1. Verificar duplicidade de email ou CNPJ
        if (companyRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Este email já está cadastrado.");
        }
        if (companyRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Este CNPJ já está cadastrado.");
        }

        // 2. Gerar databaseName e checar se já existe no DynamicDataSource
        String databaseName = "company_" + dto.getName().toLowerCase().replace(" ", "_");

        if (!(dataSource instanceof DynamicDataSource)) {
            throw new IllegalStateException("DataSource não é DynamicDataSource!");
        }
        DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;

        // Mapa atual de tenants
        Map<Object, DataSource> tenants = (Map<Object, DataSource>) dynamicDataSource.getResolvedDataSources();
        if (tenants.containsKey(databaseName)) {
            throw new BusinessException("Já existe um tenant registrado com o nome " + databaseName);
        }

        // 3. Criar o banco de dados fisicamente
        try {
            databaseService.createDatabase(databaseName);
        } catch (DataAccessException e) {
            // Se deu erro, pode ser 'database exists' ou outra coisa
            throw new BusinessException("Erro ao criar o banco de dados: " + e.getMostSpecificCause().getMessage(), e);
        }

        // 4. Initialize schema
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            throw new BusinessException("schema.sql não encontrado em resources!");
        }
        databaseService.initializeSchema(databaseName, schemaStream);

        // 5. Adicionar o novo banco ao DynamicDataSource
        String databaseUrl = "jdbc:mysql://localhost/" + databaseName;
        databaseService.addTenant(databaseName, databaseUrl, 3306, "root", "root");

        // 6. Salvar a empresa no banco principal
        Company company = new Company();
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setCnpj(dto.getCnpj());
        company.setPassword(passwordEncoder.encode(dto.getPassword()));
        company.setDatabaseUrl(databaseUrl);
        company.setDatabasePort(3306);
        company.setDatabaseName(databaseName);
        company.setCreatedAt(Instant.now());
        company.setUpdatedAt(Instant.now());

        return companyRepository.save(company);
    }
}