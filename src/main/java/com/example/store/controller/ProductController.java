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

    /**
     * * Task 4: Added a new endpoint /products to model products which appear in an order This one is Create product
     * API
     *
     * @param dto
     * @return
     */
    @PostMapping
    public ProductDTO create(@RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    /**
     * Task 4: Added a GET endpoint to return all products with order details
     *
     * @param pageable
     * @return
     */
    @GetMapping
    public Page<ProductWithOrdersDTO> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    /**
     * Task 4: Added a GET endpoint to the get product by id with order details
     *
     * @param productId
     * @return
     */
    @GetMapping("/{productId}/orders")
    public ProductWithOrdersDTO getProductWithOrders(@PathVariable Long productId) {
        return productService.getProductWithOrders(productId);
    }
}
