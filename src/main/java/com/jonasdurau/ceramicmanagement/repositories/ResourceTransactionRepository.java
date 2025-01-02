package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.entities.ResourceTransaction;

public interface ResourceTransactionRepository extends JpaRepository<ResourceTransaction, Long> {

    List<ResourceTransaction> findByResource(Resource resource);

    Optional<ResourceTransaction> findByIdAndResource(Long id, Resource resource);

    boolean existsByResourceId(Long resourceId);
}
