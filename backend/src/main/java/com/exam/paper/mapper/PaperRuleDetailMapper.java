package com.exam.paper.mapper;

import com.exam.paper.entity.PaperRuleDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperRuleDetailMapper {
    List<PaperRuleDetail> selectByRuleId(@Param("ruleId") Long ruleId);
    int insertBatch(@Param("list") List<PaperRuleDetail> list);
}
