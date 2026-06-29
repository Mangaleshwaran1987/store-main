package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /**
     * ** Implemented pagination for better performance
     *
     * @param query
     * @param pageable
     * @return
     */
    @Query(
            """
               SELECT c FROM Customer c
               WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<Customer> searchCustomers(@Param("query") String query, Pageable pageable);
}
