package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@SecurityRequirement(name = "api")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    // GET
    @GetMapping
    public ResponseEntity getAll(){
        List<Category> categories = categoryService.get();
        return ResponseEntity.ok(categories);
    }


    // POST
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity post(@RequestBody CategoryRequest categoryRequest){
        Category newCategory = categoryService.post(categoryRequest);
        return ResponseEntity.ok(newCategory);
    }
}
