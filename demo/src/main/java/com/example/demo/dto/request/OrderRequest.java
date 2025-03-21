package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;


@Data
public class OrderRequest {
    List<OrderDetailRequest> details;

    private Long discountId;
}
