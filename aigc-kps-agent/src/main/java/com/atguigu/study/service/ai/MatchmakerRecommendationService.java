package com.atguigu.study.service.ai;

import com.atguigu.study.domain.MatchmakerRecommendation;
import com.atguigu.study.mapper.MatchmakerRecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 红娘推荐记录服务
 */
@Service
public class MatchmakerRecommendationService {

    @Autowired
    private MatchmakerRecommendationMapper recommendationMapper;

    /**
     * 保存一条推荐记录
     */
    public void saveRecommendation(Long sessionId, Long userId, String partnerId, String partnerName) {
        MatchmakerRecommendation rec = MatchmakerRecommendation.builder()
                .sessionId(sessionId)
                .userId(userId)
                .partnerId(partnerId)
                .partnerName(partnerName)
                .createdAt(LocalDateTime.now())
                .build();
        recommendationMapper.insert(rec);
    }

    /**
     * 批量保存推荐记录
     * @param partnerList 每个元素是 [partnerId, partnerName]
     */
    public void saveRecommendations(Long sessionId, Long userId, List<String[]> partnerList) {
        for (String[] p : partnerList) {
            saveRecommendation(sessionId, userId, p[0], p[1]);
        }
    }

    /**
     * 查询用户已推荐过的所有明星ID
     */
    public List<String> getRecommendedPartnerIds(Long userId) {
        return recommendationMapper.selectPartnerIdsByUserId(userId);
    }

    /**
     * 查询某个会话的推荐记录
     */
    public List<MatchmakerRecommendation> getBySessionId(Long sessionId) {
        return recommendationMapper.selectBySessionId(sessionId);
    }
}