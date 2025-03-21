package com.example.demo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Tên chương trình giảm giá
    private String description; // Mô tả

    @Min(0)
    @Max(100)
    private Float globalDiscountPercentage; // Phần trăm giảm giá

    @Min(0)
    private Float maxDiscountAmount; // Số tiền giảm tối đa

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate; // Ngày bắt đầu

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate; // Ngày kết thúc

    // Có thể thêm các trường khác như: loại giảm giá, điều kiện áp dụng...
}
