package com.example.store.controller;

import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductWithOrdersDTO;
import com.example.store.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    @GetMapping
    public Page<ProductWithOrdersDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{productId}/orders")
    public ProductWithOrdersDTO getProductWithOrders(@PathVariable Long productId) {
        return productService.getProductWithOrders(productId);
    }
}
