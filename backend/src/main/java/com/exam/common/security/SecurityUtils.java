package com.exam.common.security;

import com.exam.common.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static LoginUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser) {
            return (LoginUser) auth.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }

    public static Long getCurrentUserId() {
        return getLoginUser().getUserId();
    }

    public static String getCurrentUsername() {
        return getLoginUser().getUsername();
    }

    public static boolean hasRole(String role) {
        LoginUser user = getLoginUser();
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}
