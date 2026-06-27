package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "customer-search", allEntries = true) // Removing cached values while adding new customer
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.create(customerDTO);
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAll();
    }

    /**
     * Task 2 : Extend the customer endpoint to find customers based on a query string to match a substring of one of
     * the words in their name
     *
     * @param queryStr
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/searchCustomers")
    public Page<CustomerDTO> searchCustomers(
            @RequestParam String queryStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerService.search(queryStr, pageable);
    }
}
