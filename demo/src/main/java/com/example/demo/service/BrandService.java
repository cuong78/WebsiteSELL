package com.example.demo.service;


import com.example.demo.dto.request.BrandRequest;
import com.example.demo.entity.Brand;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    // Lấy danh sách tất cả brand
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // Tạo mới brand
    public Brand createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new DuplicateKeyException("Brand name already exists");

        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        return brandRepository.save(brand);
    }

    // Cập nhật brand
    public Brand updateBrand(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        if (brandRepository.existsByName(request.getName()) && !brand.getName().equals(request.getName())) {
            throw new DuplicateKeyException("Brand name already exists");
        }

        brand.setName(request.getName());
        return brandRepository.save(brand);
    }

    // Xóa mềm brand
    public Brand softDeleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        brand.setDeleted(true);
        return brandRepository.save(brand);
    }

    // Tìm kiếm brand theo tên
    public List<Brand> searchBrands(String name) {
        return brandRepository.findByNameContainingIgnoreCase(name);
    }

    // Lấy brand theo ID
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }
}
