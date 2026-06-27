package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testCreateCustomer() {

        CustomerDTO input = new CustomerDTO();
        input.setName("Mangal");

        Customer entity = new Customer();
        entity.setName("Mangal");

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("Mangal");

        CustomerDTO output = new CustomerDTO();
        output.setId(1L);
        output.setName("Mangal");

        Mockito.when(customerMapper.toEntity(input)).thenReturn(entity);
        Mockito.when(customerRepository.save(entity)).thenReturn(saved);
        Mockito.when(customerMapper.toDto(saved)).thenReturn(output);

        CustomerDTO result = customerService.create(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mangal", result.getName());
    }

    @Test
    void testGetAllCustomers() {

        Customer entity = new Customer();
        entity.setId(1L);
        entity.setName("Mangal");

        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setName("Mangal");

        Mockito.when(customerRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        Page<CustomerDTO> result = customerService.getAll(PageRequest.of(0, 1));
        assertEquals(1, result.getTotalElements());
    }
}
