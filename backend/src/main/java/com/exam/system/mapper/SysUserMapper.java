package com.exam.system.mapper;

import com.exam.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser selectById(@Param("id") Long id);
    SysUser selectByUsername(@Param("username") String username);
    List<SysUser> selectList(@Param("keyword") String keyword, @Param("userType") Integer userType,
                             @Param("status") Integer status, @Param("classId") Long classId,
                             @Param("offset") int offset, @Param("limit") int limit);
    long countList(@Param("keyword") String keyword, @Param("userType") Integer userType,
                   @Param("status") Integer status, @Param("classId") Long classId);
    int insert(SysUser user);
    int update(SysUser user);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int softDelete(@Param("id") Long id);
    int physicalDelete(@Param("id") Long id);
    int deleteUserRoleByUserId(@Param("userId") Long userId);
}
