package com.exam.system.mapper;

import com.exam.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    SysRole selectById(@Param("id") Long id);
    SysRole selectByCode(@Param("roleCode") String roleCode);
    List<SysRole> selectAll();
    List<SysRole> selectByUserId(@Param("userId") Long userId);
    int insert(SysRole role);
    int update(SysRole role);
}
