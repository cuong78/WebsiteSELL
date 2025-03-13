package com.example.demo.entity.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;

    // Bỏ trường token vì đã nhận qua parameter
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
