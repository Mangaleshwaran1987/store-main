package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private static final Logger log = LogManager.getLogger(CustomerService.class);
    private static final String logBegin = " - start";
    private static final String logEnd = " - end";

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    // Removing cached values while adding new customer
    @Caching(
            evict = {
                @CacheEvict(value = "customer-search", allEntries = true),
                @CacheEvict(value = "customers", allEntries = true)
            })
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    public CustomerDTO create(CustomerDTO dto) {
        log.info(String.format("createAPI%s", logBegin));
        Customer entity = customerMapper.toEntity(dto);
        log.info(String.format("createAPI%s", logEnd));
        return customerMapper.toDto(customerRepository.save(entity));
    }

    @Cacheable(
            value = "customers",
            key = "T(String).format('%d-%d-%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "customerDB", fallbackMethod = "fallbackMessage")
    public Page<CustomerDTO> getAll(Pageable pageable) {
        log.info(String.format("getALL API%s", logBegin));
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        List<CustomerDTO> customersToCustomerDTOs = customerMapper.customersToCustomerDTOs(customerPage.getContent());
        log.info(String.format("getALL API%s", logEnd));
        return new PageImpl<>(customersToCustomerDTOs, pageable, customerPage.getTotalElements());
    }

    @Cacheable(
            value = "customer-search",
            key = "T(String).format('%s-%d-%d', #queryStr, #pageable.pageNumber, #pageable.pageSize)")
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "customerDB", fallbackMethod = "fallbackMessage")
    public Page<CustomerDTO> search(String queryStr, Pageable pageable) {
        log.info(String.format("search API%s", logBegin));
        // Fetch customers based on query string with pagination
        Page<Customer> customerPage = customerRepository.searchCustomers(queryStr.toLowerCase(), pageable);
        List<Customer> customers = customerPage.getContent();
        List<CustomerDTO> customersToCustomerDTOs = customerMapper.customersToCustomerDTOs(customers);
        log.info(String.format("search API%s", logEnd));
        return new PageImpl<>(customersToCustomerDTOs, pageable, customerPage.getTotalElements());
    }

    public Page<CustomerDTO> fallbackMessage(Pageable pageable, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Apologize!.. Too many requests, please retry after sometime.");
    }

    public Page<CustomerDTO> fallbackMessage(String queryStr, Pageable pageable, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Apologize!.. Too many requests, please retry after sometime.");
    }

    public Page<CustomerDTO> rateLimitFallback(Pageable pageable, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, "Apologize!.. Too many requests, please retry after sometime.");
    }

    public Page<CustomerDTO> rateLimitFallback(String queryStr, Pageable pageable, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, "Apologize!.. Too many requests, please retry after sometime.");
    }

    public CustomerDTO rateLimitFallback(CustomerDTO dto, Throwable ex) {
        throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, "Apologize!.. Too many requests, please retry after sometime.");
    }

    /**
     * To Test the circuit breaker
     *
     * @return
     */
    /*@CircuitBreaker(name = "customerDB", fallbackMethod = "fallbackMessage")
    public Page<CustomerDTO> triggerCircuitBreaker(Pageable pageable) {
        throw new RuntimeException("Random failure");
    }*/

}
