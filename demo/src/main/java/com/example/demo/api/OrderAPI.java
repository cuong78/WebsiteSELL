package com.example.demo.api;

import com.example.demo.entity.Order;
import com.example.demo.entity.request.OrderRequest;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@SecurityRequirement(name = "api")
public class OrderAPI {

    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity create(@RequestBody OrderRequest orderRequest) throws Exception {
      String urlPayment = orderService.create(orderRequest);
        return ResponseEntity.ok(urlPayment);
    }


    @GetMapping("/All")
    public ResponseEntity getAll(){
        List<Order> orders = orderService.getALL();
        return ResponseEntity.ok(orders);
    }



    @GetMapping("/user")
    public ResponseEntity getOrderByUser() {
        List<Order> order = orderService.getOrderByUser();
        return ResponseEntity.ok(order);
    }

    @PatchMapping("{id}")
    // update 1 cái
    public ResponseEntity updateStatus(@RequestParam OrderStatus status, @PathVariable Long id) {
        Order order = orderService.updateStatus(status,id);
                return ResponseEntity.ok(order);
    }

    // Lọc đơn hàng
    @GetMapping("/filter")
    public ResponseEntity<List<Order>> filterOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<Order> orders = orderService.filterOrders(status, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    // Thống kê đơn hàng
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = orderService.getOrderStatistics();
        return ResponseEntity.ok(stats);
    }


}
