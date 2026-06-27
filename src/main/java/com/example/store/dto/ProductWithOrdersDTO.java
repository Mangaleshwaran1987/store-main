package com.example.store.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductWithOrdersDTO {

    private Long id;
    private String description;
    private List<OrderSummaryDTO> orders;
}
