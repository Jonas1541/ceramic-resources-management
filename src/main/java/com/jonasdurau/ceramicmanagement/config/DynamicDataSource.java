package com.jonasdurau.ceramicmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    public DynamicDataSource(DataSource mainDataSource) {
        targetDataSources.put("main_db", mainDataSource);
        super.setTargetDataSources(targetDataSources);
        super.setDefaultTargetDataSource(mainDataSource);
    }

    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("DynamicDataSource: Buscando DataSource. Chave do TenantContext: '{}'", tenantId);
        return tenantId;
    }

    public void addDataSource(String tenantId, DataSource dataSource) {
        logger.info("Adicionando novo DataSource para o tenant: {}", tenantId);
        targetDataSources.put(tenantId, dataSource);
        super.afterPropertiesSet();
    }

    public void removeDataSource(String tenantId) {
        logger.warn("Removendo DataSource para o tenant: {}", tenantId);
        Object dataSource = targetDataSources.remove(tenantId);
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
            logger.info("Pool de conexões Hikari para o tenant {} fechado.", tenantId);
        }
        super.afterPropertiesSet();
    }
}