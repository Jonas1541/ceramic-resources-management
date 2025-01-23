package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.ProductLine;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {

    boolean existsByName(String name);
}
