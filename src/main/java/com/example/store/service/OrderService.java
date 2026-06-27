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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderMapper orderMapper;
    private static final Logger log = LogManager.getLogger(OrderService.class);
    private static final String logBegin = " - start";
    private static final String logEnd = " - end";

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

    // Removing cached values while adding new order
    @Caching(evict = {@CacheEvict(value = "orders", allEntries = true)})
    public OrderDTO create(OrderDTO dto) {
        log.info("createAPI", logBegin);
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
        log.info("createAPI", logEnd);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Cacheable(
            value = "orders",
            key = "T(String).format('%d-%d-%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "customerDB", fallbackMethod = "fallbackMessage")
    public Page<OrderDTO> getAll(Pageable pageable) {
        log.info("getAll order API", logBegin);
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
            log.info("getAll order API", logEnd);
            return dto;
        });
    }

    public OrderDTO getById(Long id) {
        log.info("getOrder by Id API", logBegin);
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
        log.info("getOrder by Id API", logEnd);
        return dto;
    }

    public Page<Customer> fallbackMessage(Pageable pageable, Throwable ex) {
        Customer fallbackUser = new Customer();
        String fallBack =
                """
                Apologize!.. There is some downstream connectivity issue, please retry after sometime.!
                """;
        fallbackUser.setName(fallBack);
        return new PageImpl<>(List.of(fallbackUser), pageable, 1);
    }

    public Page<Customer> rateLimitFallback(Pageable pageable, Throwable ex) {
        Customer fallbackUser = new Customer();
        String fallBack =
                """
                Apologize!.. Too many requests, please retry after sometime.!
                """;
        fallbackUser.setName(fallBack);
        return new PageImpl<>(List.of(fallbackUser), pageable, 1);
    }
}
