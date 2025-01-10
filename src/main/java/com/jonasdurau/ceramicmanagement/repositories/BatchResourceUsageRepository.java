package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.BatchResourceUsage;

public interface BatchResourceUsageRepository extends JpaRepository<BatchResourceUsage, Long> {

    boolean existsByResourceId(Long resourceId);
}
