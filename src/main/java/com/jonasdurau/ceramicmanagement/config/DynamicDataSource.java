package com.jonasdurau.ceramicmanagement.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // Retorna o tenant atual configurado no contexto
        return TenantContext.getCurrentTenant();
    }
}

