package com.exam.auth.dto.response;

import java.util.List;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserInfo user;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private Integer userType;
        private List<String> roles;
        private String className;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public Integer getUserType() { return userType; }
        public void setUserType(Integer userType) { this.userType = userType; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
    }
}
