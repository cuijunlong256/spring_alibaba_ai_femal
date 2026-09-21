package com.atguigu.study.mapper;

import com.atguigu.study.domain.KnowledgeArticle;
import com.atguigu.study.domain.KnowledgeArticleExample;
import com.atguigu.study.domain.KnowledgeArticleWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface KnowledgeArticleMapper {
    long countByExample(KnowledgeArticleExample example);

    int deleteByExample(KnowledgeArticleExample example);

    int deleteByPrimaryKey(String id);

    int insert(KnowledgeArticleWithBLOBs record);

    int insertSelective(KnowledgeArticleWithBLOBs record);

    List<KnowledgeArticleWithBLOBs> selectByExampleWithBLOBs(KnowledgeArticleExample example);

    List<KnowledgeArticle> selectByExample(KnowledgeArticleExample example);

    KnowledgeArticleWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") KnowledgeArticleWithBLOBs record, @Param("example") KnowledgeArticleExample example);

    int updateByExampleWithBLOBs(@Param("record") KnowledgeArticleWithBLOBs record, @Param("example") KnowledgeArticleExample example);

    int updateByExample(@Param("record") KnowledgeArticle record, @Param("example") KnowledgeArticleExample example);

    int updateByPrimaryKeySelective(KnowledgeArticleWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(KnowledgeArticleWithBLOBs record);

    int updateByPrimaryKey(KnowledgeArticle record);
}