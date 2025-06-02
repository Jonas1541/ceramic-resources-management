package com.jonasdurau.ceramicmanagement.dtos.response;

import java.time.Instant;

public record DeletionStatusResponseDTO(
    boolean isMarkedForDeletion,
    Instant deletionScheduledAt
) {}
