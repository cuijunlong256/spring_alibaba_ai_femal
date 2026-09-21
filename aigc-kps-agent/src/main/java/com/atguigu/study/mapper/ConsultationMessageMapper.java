package com.atguigu.study.mapper;

import com.atguigu.study.domain.ConsultationMessage;
import com.atguigu.study.domain.ConsultationMessageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConsultationMessageMapper {
    long countByExample(ConsultationMessageExample example);

    int deleteByExample(ConsultationMessageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConsultationMessage record);

    int insertSelective(ConsultationMessage record);

    List<ConsultationMessage> selectByExampleWithBLOBs(ConsultationMessageExample example);

    List<ConsultationMessage> selectByExample(ConsultationMessageExample example);

    ConsultationMessage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConsultationMessage record, @Param("example") ConsultationMessageExample example);

    int updateByExampleWithBLOBs(@Param("record") ConsultationMessage record, @Param("example") ConsultationMessageExample example);

    int updateByExample(@Param("record") ConsultationMessage record, @Param("example") ConsultationMessageExample example);

    int updateByPrimaryKeySelective(ConsultationMessage record);

    int updateByPrimaryKeyWithBLOBs(ConsultationMessage record);

    int updateByPrimaryKey(ConsultationMessage record);
}