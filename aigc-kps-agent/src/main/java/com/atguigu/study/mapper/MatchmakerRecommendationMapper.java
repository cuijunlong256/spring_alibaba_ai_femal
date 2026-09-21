package com.atguigu.study.mapper;

import com.atguigu.study.domain.MatchmakerRecommendation;
import com.atguigu.study.domain.MatchmakerRecommendationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MatchmakerRecommendationMapper {
    long countByExample(MatchmakerRecommendationExample example);

    int deleteByExample(MatchmakerRecommendationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MatchmakerRecommendation record);

    int insertSelective(MatchmakerRecommendation record);

    List<MatchmakerRecommendation> selectByExample(MatchmakerRecommendationExample example);

    MatchmakerRecommendation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MatchmakerRecommendation record, @Param("example") MatchmakerRecommendationExample example);

    int updateByExample(@Param("record") MatchmakerRecommendation record, @Param("example") MatchmakerRecommendationExample example);

    int updateByPrimaryKeySelective(MatchmakerRecommendation record);

    int updateByPrimaryKey(MatchmakerRecommendation record);

    /**
     * 查询某个用户已推荐过的所有明星ID
     */
    List<String> selectPartnerIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询某个会话的推荐记录
     */
    List<MatchmakerRecommendation> selectBySessionId(@Param("sessionId") Long sessionId);

}