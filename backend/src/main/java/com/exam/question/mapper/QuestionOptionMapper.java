package com.exam.question.mapper;

import com.exam.question.entity.QuestionOption;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionOptionMapper {

    int batchInsert(@Param("options") List<QuestionOption> options);

    int deleteByQuestionId(@Param("questionId") Long questionId);

    List<QuestionOption> selectByQuestionId(@Param("questionId") Long questionId);
}

