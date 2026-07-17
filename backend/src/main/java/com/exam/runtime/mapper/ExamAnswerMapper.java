package com.exam.runtime.mapper;

import com.exam.runtime.entity.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamAnswerMapper {
    ExamAnswer selectById(@Param("id") Long id);
    List<ExamAnswer> selectByExamPaperId(@Param("examPaperId") Long examPaperId);
    int insert(ExamAnswer answer);
    int insertBatch(@Param("list") List<ExamAnswer> list);
    int updateAnswer(ExamAnswer answer);
    int updateScore(ExamAnswer answer);
    List<ExamAnswer> selectUnmarked(@Param("examPaperId") Long examPaperId);

    // Analytics queries
    List<java.util.Map<String, Object>> selectScoreTrend(@Param("studentId") Long studentId);
    List<java.util.Map<String, Object>> selectTypeBreakdown(@Param("studentId") Long studentId);
    List<java.util.Map<String, Object>> selectQuestionAccuracy(@Param("examId") Long examId);

    // Delete by student
    int deleteByStudentId(@Param("studentId") Long studentId);
}
