package com.jonasdurau.ceramicmanagement.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // Configuração do mapa de DataSources
        Map<Object, Object> dataSources = new HashMap<>();

        // Exemplo: Adicionar um banco principal (default)
        dataSources.put("main_db", createDataSource("jdbc:mysql://localhost/main_db", 3306, "root", "root"));

        dynamicDataSource.setTargetDataSources(dataSources);
        dynamicDataSource.setDefaultTargetDataSource(createDataSource("jdbc:mysql://localhost/main_db", 3306, "root", "root"));

        return dynamicDataSource;
    }

    private DataSource createDataSource(String url, int port, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}

