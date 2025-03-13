package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    public Account toggleUserStatus(Long userId) {
        Account user = authenticationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setEnabled(!user.isEnabled());
        return authenticationRepository.save(user);
    }

    public List<Account> getAllUsers(int page, int size) {
        return authenticationRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    public Account updateUserRole(Long userId, RoleEnum newRole) {
        Account user = authenticationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Kiểm tra role hợp lệ
        if (newRole == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        // Cập nhật role
        user.setRoleEnum(newRole);
        return authenticationRepository.save(user);
    }
}
