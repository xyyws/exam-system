package com.exam.common.model;

import java.util.Collection;
import java.util.List;

public class LoginUser {
    private Long userId;
    private String username;
    private Integer userType;
    private Collection<String> roles;

    public LoginUser() {}
    public LoginUser(Long userId, String username, Integer userType, Collection<String> roles) {
        this.userId = userId;
        this.username = username;
        this.userType = userType;
        this.roles = roles;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getUserType() { return userType; }
    public void setUserType(Integer userType) { this.userType = userType; }
    public Collection<String> getRoles() { return roles; }
    public void setRoles(Collection<String> roles) { this.roles = roles; }
}
