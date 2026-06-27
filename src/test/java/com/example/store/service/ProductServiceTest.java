package com.example.store.service;

import com.example.store.dto.ProductWithOrdersDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductMapper mapper;

    @InjectMocks
    ProductService productService;

    @Test
    void testGetProductWithOrders() {

        Product product = new Product();
        product.setId(1L);

        Order order = new Order();
        order.setId(100L);

        Customer customer = new Customer();
        customer.setId(10L);
        order.setCustomer(customer);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Mockito.when(orderRepository.findByProductId(1L)).thenReturn(List.of(order));

        ProductWithOrdersDTO result = productService.getProductWithOrders(1L);

        assertEquals(1L, result.getId());
        assertEquals(1, result.getOrders().size());
    }
}
