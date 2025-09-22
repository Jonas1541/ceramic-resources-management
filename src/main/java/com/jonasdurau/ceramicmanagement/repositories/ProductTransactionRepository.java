package com.jonasdurau.ceramicmanagement.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;

public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long> {

    boolean existsByProductId(Long productId);

    List<ProductTransaction> findByProduct(Product product);

    Optional<ProductTransaction> findByIdAndProduct(Long id, Product product);

    List<ProductTransaction> findByState(ProductState state);

    List<ProductTransaction> findByProductAndStateAndOutgoingReasonIsNullOrderByCreatedAtAsc(Product product, ProductState state, Pageable pageable);

    List<ProductTransaction> findByProductAndStateAndOutgoingReasonIsNotNullOrderByCreatedAtAsc(Product product, ProductState state, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END FROM ProductTransaction pt")
    boolean anyExists();
}
