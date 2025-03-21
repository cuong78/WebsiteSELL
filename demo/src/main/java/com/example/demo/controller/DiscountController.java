package com.example.demo.controller;

import com.example.demo.entity.Discount;
import com.example.demo.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@SecurityRequirement(name = "api")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    @Operation(summary = "Get all discounts")
    public List<Discount> getAll() {
        return discountService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get discount by ID")
    public Discount getById(@PathVariable Long id) {
        return discountService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new discount")
    public Discount create(@RequestBody Discount discount) {
        return discountService.create(discount);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update discount")
    public Discount update(@PathVariable Long id, @RequestBody Discount discount) {
        return discountService.update(id, discount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete discount")
    public void delete(@PathVariable Long id) {
        discountService.delete(id);
    }
}