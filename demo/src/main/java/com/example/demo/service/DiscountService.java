package com.example.demo.service;

import com.example.demo.entity.Discount;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAll() {
        return discountRepository.findAll();
    }

    public Discount getById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
    }

    public Discount create(Discount discount) {
        validateDiscountDates(discount);
        return discountRepository.save(discount);
    }

    public Discount update(Long id, Discount discount) {
        Discount existing = getById(id);
        discount.setId(id);
        validateDiscountDates(discount);
        return discountRepository.save(discount);
    }

    public void delete(Long id) {
        discountRepository.deleteById(id);
    }

    private void validateDiscountDates(Discount discount) {
        if (discount.getStartDate() != null
                && discount.getEndDate() != null
                && discount.getStartDate().after(discount.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    // Phương thức để lấy discount đang active
    public Discount getActiveDiscount(Long discountId) {
        if (discountId == null) {
            return null; // Nếu không có discountId, trả về null
        }

        Date now = new Date();
        return discountRepository.findActiveDiscountById(discountId, now);
    }
}