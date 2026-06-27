package com.example.store.controller;

import com.example.store.dto.ProductWithOrdersDTO;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductService service;

    @Autowired
    ObjectMapper mapper;

    @Test
    void testGetAllProducts() throws Exception {

        ProductWithOrdersDTO product = new ProductWithOrdersDTO();
        product.setId(1L);

        Mockito.when(service.getAllProducts(Mockito.any())).thenReturn(new PageImpl<>(List.of(product)));

        mockMvc.perform(get("/products?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void testGetProductWithOrders() throws Exception {

        ProductWithOrdersDTO product = new ProductWithOrdersDTO();
        product.setId(1L);

        Mockito.when(service.getProductWithOrders(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
