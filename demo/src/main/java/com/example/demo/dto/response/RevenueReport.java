package com.example.demo.dto.response;

import lombok.Data;

@Data
public class RevenueReport {
    private int year;
    private int month;
    private double revenue;
}