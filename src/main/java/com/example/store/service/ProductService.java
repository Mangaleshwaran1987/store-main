package com.example.store.service;

import com.example.store.dto.OrderSummaryDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.dto.ProductWithOrdersDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Product;
import com.example.store.mapper.ProductMapper;
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
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductMapper mapper;
    private static final Logger log = LogManager.getLogger(ProductService.class);
    private static final String logBegin = " - start";
    private static final String logEnd = " - end";

    public ProductService(ProductRepository productRepository, OrderRepository orderRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }
    // Removing cached values while adding new product
    @Caching(evict = {@CacheEvict(value = "products", allEntries = true)})
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    public ProductDTO create(ProductDTO dto) {
        log.info(String.format("createAPI%s", logBegin));
        return mapper.toDto(productRepository.save(mapper.toEntity(dto)));
    }

    @Cacheable(
            value = "products",
            key = "T(String).format('%d-%d-%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    @RateLimiter(name = "customerRateLimiter", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "customerDB", fallbackMethod = "fallbackMessage")
    public Page<ProductWithOrdersDTO> getAllProducts(Pageable pageable) {
        log.info(String.format("getAllProducts API%s", logBegin));
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
            log.info(String.format("getAllProducts API%s", logEnd));
            return dto;
        });
    }

    public ProductWithOrdersDTO getProductWithOrders(Long productId) {
        log.info(String.format("getProductById API%s", logBegin));
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
        log.info(String.format("getProductById API%s", logEnd));
        return response;
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

    public Page<ProductWithOrdersDTO> rateLimitFallback(Pageable pageable, Throwable ex) {
        ProductWithOrdersDTO fallbackUser = new ProductWithOrdersDTO();
        String fallBack =
                """
                Apologize!.. Too many requests, please retry after sometime.!
                """;
        fallbackUser.setDescription(fallBack);
        return new PageImpl<>(List.of(fallbackUser), pageable, 1);
    }

    public ProductDTO rateLimitFallback(ProductDTO dto, Throwable ex) {
        ProductDTO productDTO = new ProductDTO();
        String fallBack =
                """
                Apologize!.. Too many requests, please retry after sometime.!
                """;
        productDTO.setDescription(fallBack);
        return productDTO;
    }
}
