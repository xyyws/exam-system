package com.exam.question.mapper;

import com.exam.question.dto.QuestionQueryRequest;
import com.exam.question.entity.Question;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionMapper {

    int insert(Question question);

    int updateById(Question question);

    Question selectById(@Param("id") Long id);

    List<Question> selectPage(QuestionQueryRequest request);

    long countPage(QuestionQueryRequest request);

    int softDeleteById(@Param("id") Long id);

    List<Question> selectForPaper(@Param("questionType") Integer questionType,
                                   @Param("categoryId") Long categoryId,
                                   @Param("difficulty") Integer difficulty,
                                   @Param("excludeIds") java.util.Set<Long> excludeIds);
}

