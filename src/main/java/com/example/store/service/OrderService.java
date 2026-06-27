package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderMapper orderMapper;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = mapper;
    }

    public OrderDTO create(OrderDTO dto) {

        Order order = orderMapper.toEntity(dto);

        // Set Customer
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        order.setCustomer(customer);

        // Set Products
        if (dto.getProducts() != null) {
            List<Product> products = dto.getProducts().stream()
                    .map(p -> productRepository.findById(p.getId()).orElseThrow())
                    .toList();
            order.setProducts(products);
        }

        return orderMapper.toDto(orderRepository.save(order));
    }

    public Page<OrderDTO> getAll(Pageable pageable) {

        return orderRepository.findAll(pageable).map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setDescription(order.getDescription());
            dto.setCustomerId(order.getCustomer().getId());

            List<ProductDTO> products = order.getProducts().stream()
                    .map(p -> {
                        ProductDTO productDTO = new ProductDTO();
                        productDTO.setId(p.getId());
                        productDTO.setDescription(p.getDescription());
                        return productDTO;
                    })
                    .toList();

            dto.setProducts(products);

            return dto;
        });
    }

    public OrderDTO getById(Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setCustomerId(order.getCustomer().getId());

        List<ProductDTO> products = order.getProducts().stream()
                .map(product -> {
                    ProductDTO p = new ProductDTO();
                    p.setId(product.getId());
                    p.setDescription(product.getDescription());
                    return p;
                })
                .toList();

        dto.setProducts(products);

        return dto;
    }
}
