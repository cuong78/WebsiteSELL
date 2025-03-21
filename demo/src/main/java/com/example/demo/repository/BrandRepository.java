package com.example.demo.repository;

import com.example.demo.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    // Tìm kiếm brand theo tên (exact match)
    Optional<Brand> findByName(String name);

    // Tìm kiếm brand theo tên (case-insensitive, partial match)
    List<Brand> findByNameContainingIgnoreCase(String name);

    // Kiểm tra xem brand có tồn tại với tên cụ thể hay không
    boolean existsByName(String name);

    // Lấy danh sách brand theo danh sách ID
    List<Brand> findByIdIn(List<Long> ids);

    // Lấy danh sách brand đã bị xóa mềm (nếu có hỗ trợ soft delete)
    List<Brand> findByIsDeletedTrue();

    // Lấy danh sách brand chưa bị xóa mềm (nếu có hỗ trợ soft delete)
    List<Brand> findByIsDeletedFalse();

}