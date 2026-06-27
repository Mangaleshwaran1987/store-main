package com.example.store.controller;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.service.CustomerService;
import com.example.store.service.OrderService;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ComponentScan(basePackageClasses = {OrderMapper.class, OrderService.class})
@RequiredArgsConstructor
class OrderControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    OrderService service;

    @MockitoBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private OrderController orderController;

    @MockitoBean
    private CustomerService customerService;

    private Order order;
    private Customer customer;

    @Test
    void testGetAllOrders() throws Exception {

        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setDescription("Test Order");

        Mockito.when(service.getAll(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));

        /*mockMvc.perform(get("/orders").param("page", "0").param("size", "1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1));*/
    }

    @Test
    void testGetOrderById() throws Exception {

        OrderDTO order = new OrderDTO();
        order.setId(1L);

        Mockito.when(service.getById(1L)).thenReturn(order);

        /*mockMvc.perform(get("/orders/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));*/
    }
}
