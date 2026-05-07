package com.exam.system.mapper;

import com.exam.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    List<SysUserRole> selectByUserId(@Param("userId") Long userId);
    int insert(SysUserRole userRole);
    int deleteByUserId(@Param("userId") Long userId);
    int insertBatch(@Param("list") List<SysUserRole> list);
}
