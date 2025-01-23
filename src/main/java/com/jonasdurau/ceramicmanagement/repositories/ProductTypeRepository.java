package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    boolean existsByName(String name);
}
