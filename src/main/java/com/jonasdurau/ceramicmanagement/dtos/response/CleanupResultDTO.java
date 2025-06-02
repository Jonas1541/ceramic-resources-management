package com.jonasdurau.ceramicmanagement.dtos.response;

public record CleanupResultDTO(
    int deletedCount,
    int failedCount,
    String message
) {}