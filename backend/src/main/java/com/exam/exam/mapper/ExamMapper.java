package com.exam.exam.mapper;

import com.exam.exam.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    Exam selectById(@Param("id") Long id);
    int insert(Exam exam);
    int update(Exam exam);
    int updateStatus(@Param("id") Long id, @Param("publishStatus") Integer publishStatus);
    List<Exam> selectAvailable(@Param("studentId") Long studentId, @Param("now") String now);

    List<Exam> selectList(@Param("creatorId") Long creatorId, @Param("publishStatus") Integer publishStatus,
                          @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    long countList(@Param("creatorId") Long creatorId, @Param("publishStatus") Integer publishStatus,
                   @Param("keyword") String keyword);

    long countAll();
}
