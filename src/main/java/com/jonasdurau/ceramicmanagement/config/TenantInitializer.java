// TenantInitializer.java
package com.jonasdurau.ceramicmanagement.config;

import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import com.jonasdurau.ceramicmanagement.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantInitializer implements CommandLineRunner {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DatabaseService databaseService;

    @Override
    public void run(String... args) throws Exception {
        // Carrega todas as empresas do main_db
        List<Company> companies = companyRepository.findAll();
        
        // Para cada empresa, adiciona o tenant ao DynamicDataSource
        for (Company company : companies) {
            String tenantId = company.getDatabaseName();
            String url = company.getDatabaseUrl();
            int port = company.getDatabasePort();
            // Usuário e senha assumidos fixos para simplificar:
            databaseService.addTenant(tenantId, url, port, "root", "root");
        }
    }
}
