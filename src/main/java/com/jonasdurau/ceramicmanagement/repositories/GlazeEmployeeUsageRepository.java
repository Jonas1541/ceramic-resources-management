package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeEmployeeUsage;

public interface GlazeEmployeeUsageRepository extends JpaRepository<GlazeEmployeeUsage, Long>{

    boolean existsByEmployeeId(Long employeeId);

    List<GlazeEmployeeUsage> findByEmployeeId(Long employeeId);
}
