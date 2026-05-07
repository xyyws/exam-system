package com.exam.paper.mapper;

import com.exam.paper.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperMapper {
    Paper selectById(@Param("id") Long id);

    List<Paper> selectList(@Param("keyword") String keyword, @Param("paperType") Integer paperType,
                           @Param("status") Integer status, @Param("creatorId") Long creatorId,
                           @Param("offset") int offset, @Param("limit") int limit);

    long countList(@Param("keyword") String keyword, @Param("paperType") Integer paperType,
                   @Param("status") Integer status, @Param("creatorId") Long creatorId);

    int insert(Paper paper);
    int update(Paper paper);
    int softDelete(@Param("id") Long id);
}
