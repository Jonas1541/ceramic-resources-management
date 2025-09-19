package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.GlazeFiringEmployeeUsage;

public interface GlazeFiringEmployeeUsageRepository extends JpaRepository<GlazeFiringEmployeeUsage, Long>{
    
    boolean existsByEmployeeId(Long employeeId);
}
