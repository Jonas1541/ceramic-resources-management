package com.jonasdurau.ceramicmanagement.services;

import com.jonasdurau.ceramicmanagement.config.DynamicDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createDatabase(String databaseName) {
        String createDatabaseSql = "CREATE DATABASE " + databaseName;
        jdbcTemplate.execute(createDatabaseSql);
    }

    public void initializeSchema(String databaseName, InputStream schemaStream) throws IOException {
        DataSource dataSource = createDataSourceForDatabase(databaseName);
        JdbcTemplate newDbTemplate = new JdbcTemplate(dataSource);

        // Lê o conteúdo do InputStream para uma String
        String sqlScript;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(schemaStream))) {
            sqlScript = reader.lines().collect(Collectors.joining("\n"));
        }

        // Verifica se o script SQL está vazio
        if (sqlScript == null || sqlScript.trim().isEmpty()) {
            throw new IllegalArgumentException("O script SQL está vazio.");
        }

        // Divide o script em instruções separadas usando o delimitador ";"
        String[] sqlStatements = sqlScript.split(";");

        // Executa cada instrução separadamente
        for (String statement : sqlStatements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                newDbTemplate.execute(trimmedStatement);
            }
        }
    }

    private DataSource createDataSourceForDatabase(String databaseName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost/" + databaseName);
        config.setUsername("root");
        config.setPassword("root");
        return new HikariDataSource(config);
    }

    public void addTenant(String tenantId, String url, int port, String username, String password) {
        // Verifica se o dataSource é uma instância de DynamicDataSource
        if (!(dataSource instanceof DynamicDataSource)) {
            throw new IllegalStateException("DataSource configurado não é DynamicDataSource");
        }

        DynamicDataSource dynamicDataSource = (DynamicDataSource) dataSource;

        // Cria o novo DataSource para o tenant
        DataSource newDataSource = createDataSource(url, port, username, password);

        // Obtém o mapa atual de DataSources do DynamicDataSource com casting explícito
        Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) dynamicDataSource
                .getResolvedDataSources();

        // Atualiza o mapa de DataSources com o novo tenant
        resolvedDataSources.put(tenantId, newDataSource);

        // Define o novo mapa no DynamicDataSource e reaplica as configurações
        dynamicDataSource.setTargetDataSources(new HashMap<>(resolvedDataSources));
        dynamicDataSource.afterPropertiesSet(); // Atualiza o cache interno
    }

    private DataSource createDataSource(String url, int port, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
