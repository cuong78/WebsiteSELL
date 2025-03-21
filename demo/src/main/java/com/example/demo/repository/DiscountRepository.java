package com.example.demo.repository;

import com.example.demo.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE " +
            "d.id = :discountId AND " +
            "(d.startDate IS NULL OR d.startDate <= :now) AND " +
            "(d.endDate IS NULL OR d.endDate >= :now)")
    Discount findActiveDiscountById(@Param("discountId") Long discountId, @Param("now") Date now);
}
