package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderMapper orderMapper;

    @InjectMocks
    OrderService service;

    @Test
    void testGetById() {

        // Customer
        Customer customer = new Customer();
        customer.setId(10L);

        // Product
        Product product = new Product();
        product.setId(100L);
        product.setDescription("Laptop");

        // Order
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setProducts(List.of(product));

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = service.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAllPaginated() {

        // Customer
        Customer customer = new Customer();
        customer.setId(10L);

        // Product
        Product product = new Product();
        product.setId(100L);
        product.setDescription("Laptop");

        // Order
        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setProducts(List.of(product));

        Mockito.when(orderRepository.findAll(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> result = service.getAll(PageRequest.of(0, 1));

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }
}
