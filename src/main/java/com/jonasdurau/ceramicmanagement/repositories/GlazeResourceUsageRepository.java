package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeResourceUsage;

public interface GlazeResourceUsageRepository extends JpaRepository<GlazeResourceUsage, Long> {

    boolean existsByResourceId(Long resourceId);

    List<GlazeResourceUsage> findByResourceId(Long resourceId);
}
