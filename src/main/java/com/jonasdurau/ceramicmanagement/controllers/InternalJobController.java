package com.jonasdurau.ceramicmanagement.controllers;

import com.jonasdurau.ceramicmanagement.services.CompanyCleanupService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/internal/tasks")
    public class InternalJobController {

        private static final Logger logger = LoggerFactory.getLogger(InternalJobController.class);

        @Autowired
        private CompanyCleanupService companyCleanupService;

        @PostMapping("/trigger-company-cleanup")
        public ResponseEntity<String> triggerCompanyCleanup() {
            logger.info("Recebida solicitação para executar o job de limpeza de empresas.");
            try {
                companyCleanupService.deleteInactiveCompanies();
                return ResponseEntity.ok("Job de limpeza de empresas iniciado com sucesso.");
            } catch (Exception e) {
                logger.error("Erro ao executar o job de limpeza de empresas.", e);
                return ResponseEntity.internalServerError().body("Erro ao iniciar o job de limpeza: " + e.getMessage());
            }
        }
    }
