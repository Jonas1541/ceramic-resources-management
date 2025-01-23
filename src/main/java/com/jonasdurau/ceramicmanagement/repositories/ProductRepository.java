package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByLineId(Long productLineId);

    boolean existsByTypeId(Long productTypeId);
}
