package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.config.DynamicDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

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
        JdbcTemplate newDbTemplate = new JdbcTemplate(tenantSpecificDataSource);
        String sqlScript;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(schemaStream))) {
            sqlScript = reader.lines().collect(Collectors.joining("\n"));
        }
        if (sqlScript == null || sqlScript.trim().isEmpty()) {
            throw new IllegalArgumentException("O script SQL do schema está vazio.");
        }
        String[] sqlStatements = sqlScript.split(";");
        for (String statement : sqlStatements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                newDbTemplate.execute(trimmedStatement);
            }
        }
    }

    private DataSource createDataSourceForNewTenantDB(String databaseName) {
        HikariConfig config = new HikariConfig();
        String cleanBaseUrl = tenantDbBaseUrl.endsWith("/") ? tenantDbBaseUrl.substring(0, tenantDbBaseUrl.length() -1) : tenantDbBaseUrl;
        String jdbcUrl = cleanBaseUrl + "/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true"; 
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(tenantDbUsername);
        config.setPassword(tenantDbPassword);
        return new HikariDataSource(config);
    }

    public void addTenant(String tenantId, String jdbcUrl, String username, String password) {
        if (!(dynamicDataSourceBean instanceof DynamicDataSource)) {
            throw new IllegalStateException("DataSource configurado não é DynamicDataSource");
        }
        DynamicDataSource dynamicDataSource = (DynamicDataSource) dynamicDataSourceBean;
        DataSource newTenantDataSource = createHikariDataSource(jdbcUrl, username, password);
        Map<Object, DataSource> currentResolvedDataSources = dynamicDataSource.getResolvedDataSources();
        Map<Object, Object> newTargetDataSources = new HashMap<>();
        if (currentResolvedDataSources != null) {
            newTargetDataSources.putAll(currentResolvedDataSources);
        }
        newTargetDataSources.put(tenantId, newTenantDataSource);
        dynamicDataSource.setTargetDataSources(newTargetDataSources);
        dynamicDataSource.afterPropertiesSet();
    }

    private DataSource createHikariDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}