package com.jonasdurau.ceramicmanagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    boolean existsByName(String name);

    Optional<Resource> findByCategory(ResourceCategory category);
}
