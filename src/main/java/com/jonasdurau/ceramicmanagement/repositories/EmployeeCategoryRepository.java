package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.EmployeeCategory;

public interface EmployeeCategoryRepository extends JpaRepository<EmployeeCategory, Long>{

    boolean existsByName(String name);
}
