package com.jonasdurau.ceramicmanagement.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
}