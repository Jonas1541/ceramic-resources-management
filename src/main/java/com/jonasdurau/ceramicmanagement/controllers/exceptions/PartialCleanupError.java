package com.jonasdurau.ceramicmanagement.controllers.exceptions;

import java.time.Instant;

public class PartialCleanupError extends StandardError {
    private int successCount;
    private int failureCount;

    public PartialCleanupError(Instant timestamp, int status, String error, String message, String path, int successCount, int failureCount) {
        super(timestamp, status, error, message, path);
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
}