package com.jonasdurau.ceramicmanagement.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${main.datasource.url}")
    private String mainDbUrl;

    @Value("${main.datasource.username}")
    private String mainDbUsername;

    @Value("${main.datasource.password}")
    private String mainDbPassword;

    @Bean("mainActualDataSource")
    public DataSource mainActualDataSource() {
        return createDataSource(mainDbUrl, mainDbUsername, mainDbPassword);
    }

    @Bean("dataSource")
    @Primary
    public DataSource dynamicDataSource(@Qualifier("mainActualDataSource") DataSource actualMainDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("main_db", actualMainDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(actualMainDataSource);
        return dynamicDataSource;
    }

    private DataSource createDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}