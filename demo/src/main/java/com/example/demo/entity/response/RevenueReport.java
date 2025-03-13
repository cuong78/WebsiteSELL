package com.example.demo.entity.response;

import lombok.Data;

@Data
public class RevenueReport {
    private int year;
    private int month;
    private double revenue;
}