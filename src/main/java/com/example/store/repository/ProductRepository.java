package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // EntityGraph to avoid N+1 problem. It's a lazy loading.
    @EntityGraph(attributePaths = {"orders", "orders.customer"})
    Page<Product> findAll(Pageable pageable);
}
