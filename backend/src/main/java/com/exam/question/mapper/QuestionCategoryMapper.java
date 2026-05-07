package com.exam.question.mapper;

import com.exam.question.entity.QuestionCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionCategoryMapper {
    List<QuestionCategory> selectByParentId(@Param("parentId") Long parentId);
    List<QuestionCategory> selectAll();
    QuestionCategory selectById(@Param("id") Long id);
    int insert(QuestionCategory category);
    int update(QuestionCategory category);
    int deleteById(@Param("id") Long id);
}
