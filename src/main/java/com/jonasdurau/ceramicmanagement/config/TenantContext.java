package com.jonasdurau.ceramicmanagement.config;

import org.springframework.lang.Nullable;

public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(@Nullable String tenant) {
        if (tenant == null) {
            currentTenant.remove();
        } else {
            currentTenant.set(tenant);
        }
    }

    @Nullable
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
