package com.jonasdurau.ceramicmanagement.config;

import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import com.jonasdurau.ceramicmanagement.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantInitializer implements CommandLineRunner {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DatabaseService databaseService;

    @Value("${tenant.datasource.username}")
    private String tenantDbUsername;

    @Value("${tenant.datasource.password}")
    private String tenantDbPassword;

    @Override
    public void run(String... args) throws Exception {
        TenantContext.clear(); 

        List<Company> companies = companyRepository.findAll();
        
        for (Company company : companies) {
            String tenantId = company.getDatabaseName(); 
            String jdbcUrl = company.getDatabaseUrl();   
            
            databaseService.addTenant(tenantId, jdbcUrl, tenantDbUsername, tenantDbPassword);
        }
    }
}