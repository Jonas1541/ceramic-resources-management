package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.config.DynamicDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private final JdbcTemplate mainJdbcTemplate;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dynamicDataSourceBean;

    @Value("${tenant.datasource.base-url}")
    private String tenantDbBaseUrl;

    @Value("${tenant.datasource.username}")
    private String tenantDbUsername;

    @Value("${tenant.datasource.password}")
    private String tenantDbPassword;

    @Autowired
    public DatabaseService(@Qualifier("mainActualDataSource") DataSource mainDS) {
        this.mainJdbcTemplate = new JdbcTemplate(mainDS);
    }

    public void createDatabase(String databaseName) {
        String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS `" + databaseName + "`";
        mainJdbcTemplate.execute(createDatabaseSql);
    }

    public void initializeSchema(String databaseName, InputStream schemaStream) throws IOException {
        DataSource tenantSpecificDataSource = createDataSourceForNewTenantDB(databaseName);
        InputStreamResource resource = new InputStreamResource(schemaStream);
        try (Connection connection = tenantSpecificDataSource.getConnection()) {
            connection.setCatalog(databaseName);
            ScriptUtils.executeSqlScript(connection, resource);
            logger.info("Schema para o tenant {} inicializado com sucesso.", databaseName);
        } catch (Exception e) {
            logger.error("Falha ao inicializar o schema para o tenant {}: {}", databaseName, e.getMessage(), e);
            dropTenantDatabase(databaseName);
            throw new IOException("Falha ao executar schema.sql", e);
        } finally {
            if (tenantSpecificDataSource instanceof HikariDataSource) {
                ((HikariDataSource) tenantSpecificDataSource).close();
            }
        }
    }
    
    public void addTenant(String tenantId, String jdbcUrl, String username, String password) {
        if (!(dynamicDataSourceBean instanceof DynamicDataSource)) {
            throw new IllegalStateException("DataSource configurado não é DynamicDataSource");
        }
        DynamicDataSource dynamicDataSource = (DynamicDataSource) dynamicDataSourceBean;
        DataSource newTenantDataSource = createHikariDataSource(jdbcUrl, username, password);
        dynamicDataSource.addDataSource(tenantId, newTenantDataSource);
    }

    public void dropTenantDatabase(String databaseName) {
        logger.warn("Iniciando processo de exclusão para o banco de dados do tenant: {}", databaseName);
        try {
            mainJdbcTemplate.execute("DROP DATABASE IF EXISTS `" + databaseName + "`");
            logger.info("Banco de dados do tenant {} dropado com sucesso do servidor MySQL.", databaseName);
            if (dynamicDataSourceBean instanceof DynamicDataSource) {
                ((DynamicDataSource) dynamicDataSourceBean).removeDataSource(databaseName);
            }
        } catch (Exception e) {
            logger.error("Falha ao dropar o banco de dados {} do servidor MySQL: {}", databaseName, e.getMessage(), e);
            throw new RuntimeException("Falha ao dropar o banco de dados " + databaseName + " do servidor.", e);
        }
    }

    private DataSource createDataSourceForNewTenantDB(String databaseName) {
        String cleanBaseUrl = tenantDbBaseUrl.endsWith("/") ? tenantDbBaseUrl.substring(0, tenantDbBaseUrl.length() - 1) : tenantDbBaseUrl;
        String jdbcUrl = cleanBaseUrl + "/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true";
        return createHikariDataSource(jdbcUrl, tenantDbUsername, tenantDbPassword);
    }

    private DataSource createHikariDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}