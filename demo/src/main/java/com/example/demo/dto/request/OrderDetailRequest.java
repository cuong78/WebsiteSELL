package com.example.demo.dto.request;

import lombok.Data;

@Data
public class OrderDetailRequest {
    long productId;
    int quantity;
}
