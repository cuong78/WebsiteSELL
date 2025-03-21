package com.example.demo.service;

import com.example.demo.Utils.AccountUtils;
import com.example.demo.entity.*;
import com.example.demo.dto.request.OrderDetailRequest;
import com.example.demo.dto.request.OrderRequest;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    DiscountService discountService;



    public String create(OrderRequest orderRequest) throws Exception {
        float total = 0;

        List<OrderDetail> orderDetails = new ArrayList<>();
        Order order = modelMapper.map(orderRequest, Order.class);
        order.setOrderDetails(orderDetails);
        order.setAccount(accountUtils.getCurrentAccount());

        // Tính toán giá trị đơn hàng
        for (OrderDetailRequest orderDetailRequest : orderRequest.getDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            Product product = productRepository.findProductById(orderDetailRequest.getProductId());

            if (product.getQuantity() >= orderDetailRequest.getQuantity()) {
                // Tính giá sản phẩm sau khi áp dụng giảm giá (nếu có)
                float productPrice = product.getPrice();
                if (product.getDiscountPercentage() != null && product.getDiscountPercentage() > 0) {
                    productPrice = productPrice * (1 - product.getDiscountPercentage() / 100);
                }

                orderDetail.setProduct(product);
                orderDetail.setQuantity(orderDetailRequest.getQuantity());
                orderDetail.setPrice(productPrice * orderDetailRequest.getQuantity());
                orderDetail.setOrder(order);
                orderDetails.add(orderDetail);

                // Cập nhật số lượng sản phẩm
                product.setQuantity(product.getQuantity() - orderDetailRequest.getQuantity());
                productRepository.save(product);

                // Cộng vào tổng giá trị đơn hàng
                total += orderDetail.getPrice();
            } else {
                throw new RuntimeException("Số lượng sản phẩm không đủ");
            }
        }

        // Áp dụng discount (nếu có và hợp lệ)
        if (orderRequest.getDiscountId() != null) {
            Discount discount = discountService.getActiveDiscount(orderRequest.getDiscountId());
            if (discount != null) {
                // Tính toán giảm giá
                float discountAmount = (total * discount.getGlobalDiscountPercentage() / 100);
                if (discount.getMaxDiscountAmount() != null && discountAmount > discount.getMaxDiscountAmount()) {
                    discountAmount = discount.getMaxDiscountAmount();
                }
                total -= discountAmount;
            }
        }


        order.setTotal(total);

        // Lưu đơn hàng
        Order newOrder = orderRepository.save(order);

        // Tạo URL thanh toán
        return createURLPayment(newOrder);
    }



    public String createURLPayment(Order order) throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);
        String orderId = UUID.randomUUID().toString().substring(0, 6);

        String tmnCode = "YL03W6AO";
        String secretKey = "CW4MG3MA23ALY1UYP26SNZY56TPV6J9O";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnURL = "http://localhost:8080?orderId" +order.getId();

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", (int) order.getTotal() + "00");
        vnpParams.put("vnp_ReturnUrl", returnURL);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "167.99.74.201");


        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'
        return  urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }



    public List<Order> getOrderByUser() {
        Account account = accountUtils.getCurrentAccount();
        return orderRepository.findAllByAccountId(account.getId());

    }

    public List<Order> getALL() {
        return orderRepository.findAll();
    }

    public Order updateStatus(OrderStatus orderStatus, long id) {
        Order order = orderRepository.findOrderById(id);
        order.setStatus(orderStatus);
        return orderRepository.save(order);
    }



    // Lọc đơn hàng theo trạng thái và khoảng thời gian
    public List<Order> filterOrders(OrderStatus status, Date startDate, Date endDate) {
        if (status != null && startDate != null && endDate != null) {
            return orderRepository.findByStatusAndCreateAtBetween(status, startDate, endDate);
        } else if (status != null) {
            return orderRepository.findByStatus(status);
        } else if (startDate != null && endDate != null) {
            return orderRepository.findByCreateAtBetween(startDate, endDate);
        } else {
            return orderRepository.findAll();
        }
    }

    // Thống kê đơn hàng
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Đếm số lượng đơn hàng theo trạng thái
        List<OrderStatus> statuses = List.of(OrderStatus.IN_PROGRESS, OrderStatus.PAID, OrderStatus.CANCELLED);
        for (OrderStatus status : statuses) {
            long count = orderRepository.findByStatus(status).size();
            stats.put(status.name(), count);
        }

        // Tính tổng doanh thu
        double totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .mapToDouble(Order::getTotal)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }


}
