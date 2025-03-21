package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id = 0;

    public Date createAt;
    public float total;
    public OrderStatus status = OrderStatus.IN_PROGRESS;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;


    @ManyToOne
    @JoinColumn(name = "account_id")
    public Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails = new ArrayList<>();
}
