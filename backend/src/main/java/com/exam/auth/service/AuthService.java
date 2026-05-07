package com.exam.auth.service;

import com.exam.auth.dto.request.LoginRequest;
import com.exam.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse refresh(String refreshToken);
    LoginResponse.UserInfo getCurrentUser();
    void changePassword(String oldPassword, String newPassword);
}
