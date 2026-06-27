package com.example.store.controller;

import com.example.store.configuration.CacheConfig;
import com.example.store.dto.CustomerDTO;
import com.example.store.mapper.CustomerMapper;
import com.example.store.service.CustomerService;
import com.example.store.service.OrderService;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ComponentScan(basePackageClasses = {CustomerMapper.class, CustomerService.class, CacheConfig.class})
@TestPropertySource(properties = {"spring.cache.type=none"})
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService service;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    ProductService productService;

    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void testCreateCustomer() throws Exception {

        CustomerDTO response = new CustomerDTO();
        response.setId(1L);
        response.setName("Mangal");

        Mockito.when(service.create(Mockito.any())).thenReturn(response);

        String json = """
        {
          "name": "Mangal"
        }
        """;

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mangal"));
    }

    @Test
    void testGetAllCustomers() throws Exception {

        CustomerDTO customer = new CustomerDTO();
        customer.setId(1L);
        customer.setName("Mangal");
        Mockito.when(service.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(customer)));
        mockMvc.perform(get("/customer?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Mangal"));
    }
}
