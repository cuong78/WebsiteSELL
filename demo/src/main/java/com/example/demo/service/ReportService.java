package com.example.demo.service;

import com.example.demo.dto.response.RevenueReport;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    // Thống kê doanh thu theo tháng/năm
    public List<RevenueReport> generateRevenueReport(int year, Integer month) {
        List<Object[]> results;
        if (month != null) {
            results = orderRepository.getMonthlyRevenueByYearAndMonth(year, month);
        } else {
            results = orderRepository.getMonthlyRevenueByYear(year);
        }

        return results.stream()
                .map(result -> {
                    RevenueReport report = new RevenueReport();
                    report.setYear((int) result[0]);
                    report.setMonth((int) result[1]);
                    report.setRevenue((double) result[2]);
                    return report;
                })
                .collect(Collectors.toList());
    }
}