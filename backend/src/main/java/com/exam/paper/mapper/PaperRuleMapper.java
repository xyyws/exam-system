package com.exam.paper.mapper;

import com.exam.paper.entity.PaperRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaperRuleMapper {
    PaperRule selectByPaperId(@Param("paperId") Long paperId);
    int insert(PaperRule rule);
    int deleteByPaperId(@Param("paperId") Long paperId);
}
