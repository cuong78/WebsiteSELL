package com.example.demo.mapper;

import com.example.demo.entity.Order;
import com.example.demo.dto.request.OrderRequest;
import org.modelmapper.PropertyMap;

import java.util.Date;

public class OrderMapper extends PropertyMap<OrderRequest, Order> {

    @Override
    protected void configure() {
        map().setId(0);
        map().setCreateAt(new Date());
    }
}
