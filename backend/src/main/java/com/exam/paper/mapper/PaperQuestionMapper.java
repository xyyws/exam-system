package com.exam.paper.mapper;

import com.exam.paper.entity.PaperQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperQuestionMapper {
    List<PaperQuestion> selectByPaperId(@Param("paperId") Long paperId);
    int insertBatch(@Param("list") List<PaperQuestion> list);
    int deleteByPaperId(@Param("paperId") Long paperId);
}
