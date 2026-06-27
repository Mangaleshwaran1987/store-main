package com.example.store.dto;

import lombok.Data;

@Data
public class OrderSummaryDTO {

    private Long id;
    private String description;
    private Long customerId;
}
