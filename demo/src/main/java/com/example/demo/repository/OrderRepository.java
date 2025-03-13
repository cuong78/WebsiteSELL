package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByAccountId(Long accountId);
    Order findOrderById(Long orderId);

    // Lọc đơn hàng theo trạng thái và khoảng thời gian
    List<Order> findByStatusAndCreateAtBetween(OrderStatus status, Date startDate, Date endDate);

    // Thống kê số lượng đơn hàng theo trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    // Thống kê doanh thu theo tháng/năm
    @Query("SELECT YEAR(o.createAt) AS year, MONTH(o.createAt) AS month, SUM(o.total) AS revenue " +
            "FROM Order o GROUP BY YEAR(o.createAt), MONTH(o.createAt)")
    List<Object[]> getMonthlyRevenue();

    // Tìm đơn hàng theo trạng thái
    List<Order> findByStatus(OrderStatus status);
    // Tìm đơn hàng trong khoảng thời gian
    List<Order> findByCreateAtBetween(Date startDate, Date endDate);

    // Thống kê doanh thu theo năm và tháng
    @Query("SELECT YEAR(o.createAt) AS year, MONTH(o.createAt) AS month, SUM(o.total) AS revenue " +
            "FROM Order o WHERE YEAR(o.createAt) = :year AND MONTH(o.createAt) = :month " +
            "GROUP BY YEAR(o.createAt), MONTH(o.createAt)")
    List<Object[]> getMonthlyRevenueByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Thống kê doanh thu theo năm
    @Query("SELECT YEAR(o.createAt) AS year, MONTH(o.createAt) AS month, SUM(o.total) AS revenue " +
            "FROM Order o WHERE YEAR(o.createAt) = :year " +
            "GROUP BY YEAR(o.createAt), MONTH(o.createAt)")
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);
}
