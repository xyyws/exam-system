package com.exam.analytics.mapper;

import com.exam.analytics.entity.WrongQuestionBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WrongQuestionBookMapper {

    List<WrongQuestionBook> selectByStudent(@Param("studentId") Long studentId,
                                             @Param("offset") int offset, @Param("limit") int limit);

    long countByStudent(@Param("studentId") Long studentId);

    int updateMasteredStatus(@Param("id") Long id, @Param("masteredStatus") Integer masteredStatus);

    int updateMasteredByStudentAndQuestion(@Param("studentId") Long studentId,
                                            @Param("questionId") Long questionId,
                                            @Param("masteredStatus") Integer masteredStatus);

    List<java.util.Map<String, Object>> selectSummaryByStudent(@Param("studentId") Long studentId);

    int upsert(WrongQuestionBook book);

    int updateNote(@Param("studentId") Long studentId,
                   @Param("questionId") Long questionId,
                   @Param("noteText") String noteText);
}
