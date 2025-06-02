package com.jonasdurau.ceramicmanagement.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    @Nullable
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    public Connection getConnection() throws SQLException {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            determineCurrentLookupKey();
        }
        return super.getConnection();
    }
}