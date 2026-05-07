package com.exam.auth.controller;

import com.exam.auth.dto.request.ChangePasswordRequest;
import com.exam.auth.dto.request.LoginRequest;
import com.exam.auth.dto.response.LoginResponse;
import com.exam.auth.service.AuthService;
import com.exam.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Strip "Bearer "
        authService.logout(token);
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse.UserInfo> me() {
        return ApiResponse.success(authService.getCurrentUser());
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@RequestBody String refreshToken) {
        // Extract bare token
        String token = refreshToken.replaceAll("\"", "").trim();
        return ApiResponse.success(authService.refresh(token));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ApiResponse.success();
    }
}
