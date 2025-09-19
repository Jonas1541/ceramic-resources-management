package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.BisqueFiringEmployeeUsage;

public interface BisqueFiringEmployeeUsageRepository extends JpaRepository<BisqueFiringEmployeeUsage, Long>{
    
    boolean existsByEmployeeId(Long employeeId);
}
