package com.jonasdurau.ceramicmanagement.repositories;

import com.jonasdurau.ceramicmanagement.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
