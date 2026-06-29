package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Get orders by product ID, using JOIN Query to avoid N+1 problem
    @Query("SELECT o FROM Order o JOIN o.products p WHERE p.id = :productId")
    List<Order> findByProductId(@Param("productId") Long productId);

    /**
     * Implemented EntityGraph to avoid N+1 problem. Task 4 : Change the orders endpoint to return a list of products
     * contained in the order
     *
     * @param pageable the pageable to request a paged result, can be {@link Pageable#unpaged()}, must not be
     *     {@literal null}.
     * @return
     */
    @EntityGraph(attributePaths = {"products", "customer"})
    Page<Order> findAll(Pageable pageable);
}
