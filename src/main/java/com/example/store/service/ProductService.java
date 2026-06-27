package com.example.store.service;

import com.example.store.dto.OrderSummaryDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductWithOrdersDTO;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public ProductDTO create(ProductDTO dto) {
        return mapper.toDto(productRepository.save(mapper.toEntity(dto)));
    }

    public Page<ProductWithOrdersDTO> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable).map(product -> {
            ProductWithOrdersDTO dto = new ProductWithOrdersDTO();
            dto.setId(product.getId());
            dto.setDescription(product.getDescription());

            List<OrderSummaryDTO> orders = product.getOrders().stream()
                    .map(order -> {
                        OrderSummaryDTO o = new OrderSummaryDTO();
                        o.setId(order.getId());
                        o.setDescription(order.getDescription());
                        o.setCustomerId(order.getCustomer().getId());
                        return o;
                    })
                    .toList();

            dto.setOrders(orders);

            return dto;
        });
    }

    public ProductWithOrdersDTO getProductWithOrders(Long productId) {

        Product product =
                productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        List<OrderSummaryDTO> orders = orderRepository.findByProductId(productId).stream()
                .map(order -> {
                    OrderSummaryDTO dto = new OrderSummaryDTO();
                    dto.setId(order.getId());
                    dto.setDescription(order.getDescription());
                    dto.setCustomerId(order.getCustomer().getId());
                    return dto;
                })
                .toList();

        ProductWithOrdersDTO response = new ProductWithOrdersDTO();
        response.setId(product.getId());
        response.setDescription(product.getDescription());
        response.setOrders(orders);

        return response;
    }
}
