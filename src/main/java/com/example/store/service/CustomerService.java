package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDTO create(CustomerDTO dto) {
        Customer entity = customerMapper.toEntity(dto);
        return customerMapper.toDto(customerRepository.save(entity));
    }

    public List<CustomerDTO> getAll() {
        return customerRepository.findAll().stream().map(customerMapper::toDto).toList();
    }

    @Cacheable(
            value = "customer-search",
            key = "T(String).format('%s-%d-%d', #query, #pageable.pageNumber, #pageable.pageSize)")
    public Page<CustomerDTO> search(String queryStr, Pageable pageable) {
        System.out.println("DB Hit");
        // Fetch customers based on query string with pagination
        Page<Customer> customerPage = customerRepository.searchCustomers(queryStr.toLowerCase(), pageable);
        List<Customer> customers = customerPage.getContent();
        // Fetch orders in batch by sending matched customers to avoid N+1 Problem and map itn Customer DTO
        List<CustomerDTO> customersToCustomerDTOs = customerMapper.customersToCustomerDTOs(customers);
        return new PageImpl<>(customersToCustomerDTOs, pageable, customerPage.getTotalElements());
    }
}
