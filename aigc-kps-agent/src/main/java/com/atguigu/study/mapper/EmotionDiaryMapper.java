package com.atguigu.study.mapper;

import com.atguigu.study.domain.EmotionDiary;
import com.atguigu.study.domain.EmotionDiaryExample;
import com.atguigu.study.domain.EmotionDiaryWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmotionDiaryMapper {
    long countByExample(EmotionDiaryExample example);

    int deleteByExample(EmotionDiaryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(EmotionDiaryWithBLOBs record);

    int insertSelective(EmotionDiaryWithBLOBs record);

    List<EmotionDiaryWithBLOBs> selectByExampleWithBLOBs(EmotionDiaryExample example);

    List<EmotionDiary> selectByExample(EmotionDiaryExample example);

    EmotionDiaryWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") EmotionDiaryWithBLOBs record, @Param("example") EmotionDiaryExample example);

    int updateByExampleWithBLOBs(@Param("record") EmotionDiaryWithBLOBs record, @Param("example") EmotionDiaryExample example);

    int updateByExample(@Param("record") EmotionDiary record, @Param("example") EmotionDiaryExample example);

    int updateByPrimaryKeySelective(EmotionDiaryWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(EmotionDiaryWithBLOBs record);

    int updateByPrimaryKey(EmotionDiary record);
}