package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.request.ProductRequest;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    public List<Product> getAllProduct(){
        return productRepository.findProductsByIsDeletedFalse();
    }

    public Product create(ProductRequest productRequest){
        // product request => product entity
        Product product = modelMapper.map(productRequest, Product.class);
        Category category = categoryRepository.findCategoryById(productRequest.categoryId);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product delete(long id){
        Product product = productRepository.findProductById(id);
        product.isDeleted = true;
        return productRepository.save(product);
    }

    public List<Product> searchProducts(String keyword, Long categoryId) {
        if (keyword == null && categoryId == null) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(keyword, categoryId);
    }

    // Cập nhật sản phẩm
    public Product updateProduct(long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Cập nhật thông tin sản phẩm
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImage(request.getImage());
        product.setCode(request.getCode());
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);


        return productRepository.save(product);
    }
}
