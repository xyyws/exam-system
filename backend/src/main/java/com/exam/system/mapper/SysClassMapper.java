package com.exam.system.mapper;

import com.exam.system.entity.SysClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysClassMapper {
    SysClass selectById(@Param("id") Long id);
    List<SysClass> selectList(@Param("keyword") String keyword, @Param("status") Integer status,
                              @Param("offset") int offset, @Param("limit") int limit);
    long countList(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(SysClass sysClass);
    int update(SysClass sysClass);
    int deleteById(@Param("id") Long id);
}
