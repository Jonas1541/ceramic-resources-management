package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.config.TenantContext;
import com.jonasdurau.ceramicmanagement.entities.Company;
import com.jonasdurau.ceramicmanagement.repositories.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CompanyCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyCleanupService.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DatabaseService databaseService;

    @Transactional
    public void deleteInactiveCompanies() {
        TenantContext.clear();
        logger.info("Iniciando job de limpeza de empresas inativas...");
        Instant cutoffDate = Instant.now().minus(365, ChronoUnit.DAYS);
        List<Company> inactiveCompanies = companyRepository.findInactiveCompanies(cutoffDate);
        if (inactiveCompanies.isEmpty()) {
            logger.info("Nenhuma empresa inativa encontrada para exclusão.");
            return;
        }
        logger.info("Encontradas {} empresas inativas para possível exclusão.", inactiveCompanies.size());
        for (Company company : inactiveCompanies) {
            logger.warn("Processando exclusão por inatividade para a empresa: {} (ID: {}, Email: {}, Última Atividade: {}, Criada em: {})",
                    company.getName(), company.getId(), company.getEmail(), company.getLastActivityAt(), company.getCreatedAt());
            try {
                databaseService.dropTenantDatabase(company.getDatabaseName());
                companyRepository.delete(company);
                logger.info("Empresa inativa {} (ID: {}) e seu banco de dados foram excluídos com sucesso.", company.getName(), company.getId());
                // Opcional: Enviar um email informando sobre a exclusão (se houver um email de contato válido e política para isso)
            } catch (Exception e) {
                logger.error("Falha ao processar a exclusão da empresa inativa {} (ID: {}): {}",
                        company.getName(), company.getId(), e.getMessage(), e);
            }
        }
        logger.info("Job de limpeza de empresas inativas concluído.");
    }
}
