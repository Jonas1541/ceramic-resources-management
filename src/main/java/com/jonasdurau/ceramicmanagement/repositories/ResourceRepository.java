package com.jonasdurau.ceramicmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    boolean existsByName(String name);
}
