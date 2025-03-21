package com.example.demo.mapper;

import com.example.demo.entity.Product;
import com.example.demo.dto.request.ProductRequest;
import org.modelmapper.PropertyMap;

public class ProductMapper extends PropertyMap<ProductRequest, Product> {

    @Override
    protected void configure() {
        map().setId(0);
    }
}
