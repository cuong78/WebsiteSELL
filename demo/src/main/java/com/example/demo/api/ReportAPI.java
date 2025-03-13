package com.example.demo.api;

import com.example.demo.entity.response.RevenueReport;
import com.example.demo.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/ReportAPI")
@SecurityRequirement(name = "api")
public class ReportAPI {
    @Autowired
    ReportService reportService;



    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReport>> getRevenueReport(
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        List<RevenueReport> revenueReports = reportService.generateRevenueReport(year, month);
        return ResponseEntity.ok(revenueReports);
    }
}
