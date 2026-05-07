package com.exam.system.service;

import com.exam.common.web.PageResponse;
import com.exam.system.dto.request.UserCreateRequest;
import com.exam.system.dto.request.UserQueryRequest;
import com.exam.system.dto.request.UserUpdateRequest;
import com.exam.system.dto.response.UserResponse;

public interface UserService {
    PageResponse<UserResponse> listUsers(UserQueryRequest request);
    UserResponse getUser(Long id);
    Long createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void updateStatus(Long id, Integer status);
    void assignRoles(Long id, java.util.List<Long> roleIds);
    void resetPassword(Long id, String newPassword);
    void deleteUser(Long id);
}
