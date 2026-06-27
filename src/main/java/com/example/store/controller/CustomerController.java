package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Create Customer API
     *
     * @param customerDTO
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.create(customerDTO);
    }

    /**
     * * Included Pagination for better performance Return customer details
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerService.getAll(pageable);
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

    /*@GetMapping("/enableCB")
    public Page<CustomerDTO> testCircuitBreaker(Pageable pageable) {
        return customerService.triggerCircuitBreaker(pageable);
    }*/
}
