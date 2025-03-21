package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.dto.request.ProductRequest;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "api")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Product newProduct = productService.createProduct(productRequest, imageFile);
        return ResponseEntity.ok(newProduct);
    }

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get products with pagination")
    public ResponseEntity<Page<Product>> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(productService.getProductsPaginated(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }



    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(productService.updateProduct(id, productRequest, imageFile));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Soft delete a product")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.softDeleteProduct(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by keyword, category, and/or brand")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId) {

        return ResponseEntity.ok(productService.searchProducts(keyword, categoryId, brandId));
    }

    @PatchMapping("/{id}/discount")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Update product discount percentage")
    public ResponseEntity<Product> updateDiscount(
            @PathVariable Long id,
            @RequestParam @Min(0) @Max(100) Float discountPercentage) {

        return ResponseEntity.ok(productService.updateDiscount(id, discountPercentage));
    }
}