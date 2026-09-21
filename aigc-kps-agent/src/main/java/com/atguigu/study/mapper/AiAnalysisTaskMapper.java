package com.atguigu.study.mapper;

import com.atguigu.study.domain.AiAnalysisTask;
import com.atguigu.study.domain.AiAnalysisTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AiAnalysisTaskMapper {
    long countByExample(AiAnalysisTaskExample example);

    int deleteByExample(AiAnalysisTaskExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AiAnalysisTask record);

    int insertSelective(AiAnalysisTask record);

    List<AiAnalysisTask> selectByExampleWithBLOBs(AiAnalysisTaskExample example);

    List<AiAnalysisTask> selectByExample(AiAnalysisTaskExample example);

    AiAnalysisTask selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AiAnalysisTask record, @Param("example") AiAnalysisTaskExample example);

    int updateByExampleWithBLOBs(@Param("record") AiAnalysisTask record, @Param("example") AiAnalysisTaskExample example);

    int updateByExample(@Param("record") AiAnalysisTask record, @Param("example") AiAnalysisTaskExample example);

    int updateByPrimaryKeySelective(AiAnalysisTask record);

    int updateByPrimaryKeyWithBLOBs(AiAnalysisTask record);

    int updateByPrimaryKey(AiAnalysisTask record);
}