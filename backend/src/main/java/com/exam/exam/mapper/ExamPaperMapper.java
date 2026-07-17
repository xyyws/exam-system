package com.exam.exam.mapper;

import com.exam.exam.entity.ExamPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamPaperMapper {
    ExamPaper selectById(@Param("id") Long id);
    ExamPaper selectByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
    int insert(ExamPaper examPaper);
    int update(ExamPaper examPaper);
    int updateStatus(@Param("id") Long id, @Param("answerStatus") Integer answerStatus);
    List<ExamPaper> selectByExamId(@Param("examId") Long examId);
    List<ExamPaper> selectByStudentId(@Param("studentId") Long studentId);

    Map<String, Object> selectExamStats(@Param("examId") Long examId);
    List<Map<String, Object>> selectExamRankings(@Param("examId") Long examId,
                                                  @Param("offset") int offset, @Param("limit") int limit);
    long countExamParticipants(@Param("examId") Long examId);
    List<Map<String, Object>> selectMonitorByExamId(@Param("examId") Long examId);

    Map<String, Object> selectOngoingByStudentId(@Param("studentId") Long studentId);

    long countParticipants();

    List<Map<String, Object>> selectParticipants(@Param("studentName") String studentName,
                                                  @Param("offset") int offset, @Param("limit") int limit);

    long countParticipantsByCondition(@Param("studentName") String studentName);

    int deleteByStudentId(@Param("studentId") Long studentId);
}
