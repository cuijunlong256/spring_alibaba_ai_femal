package com.atguigu.study.mapper;

import com.atguigu.study.domain.KnowledgeCategory;
import com.atguigu.study.domain.KnowledgeCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface KnowledgeCategoryMapper {
    long countByExample(KnowledgeCategoryExample example);

    int deleteByExample(KnowledgeCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(KnowledgeCategory record);

    int insertSelective(KnowledgeCategory record);

    List<KnowledgeCategory> selectByExampleWithBLOBs(KnowledgeCategoryExample example);

    List<KnowledgeCategory> selectByExample(KnowledgeCategoryExample example);

    KnowledgeCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") KnowledgeCategory record, @Param("example") KnowledgeCategoryExample example);

    int updateByExampleWithBLOBs(@Param("record") KnowledgeCategory record, @Param("example") KnowledgeCategoryExample example);

    int updateByExample(@Param("record") KnowledgeCategory record, @Param("example") KnowledgeCategoryExample example);

    int updateByPrimaryKeySelective(KnowledgeCategory record);

    int updateByPrimaryKeyWithBLOBs(KnowledgeCategory record);

    int updateByPrimaryKey(KnowledgeCategory record);
}