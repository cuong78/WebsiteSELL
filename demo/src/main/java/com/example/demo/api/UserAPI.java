package com.example.demo.api;

import com.example.demo.entity.Account;
import com.example.demo.enums.RoleEnum;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api")
public class UserAPI {

    @Autowired
    private UserService userService;

    // Lấy tất cả user (phân trang)
    @GetMapping
    public ResponseEntity<List<Account>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    // Cập nhật role user
    @PutMapping("/{userId}/role")
    public ResponseEntity<Account> updateUserRole(
            @PathVariable Long userId,
            @RequestParam RoleEnum newRole) {
        Account updatedUser = userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    // Khóa/mở tài khoản
    @PutMapping("/{userId}/status")
    public ResponseEntity<Account> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }
}
