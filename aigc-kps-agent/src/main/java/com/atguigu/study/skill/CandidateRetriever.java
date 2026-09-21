package com.atguigu.study.skill;

import com.atguigu.study.domain.MatchmakerRecommendation;
import com.atguigu.study.domain.Partner;
import com.atguigu.study.dto.MatchRequest;
import com.atguigu.study.mapper.MatchmakerRecommendationMapper;
import com.atguigu.study.pgmapper.PartnerMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 候选集检索器：根据 MatchRequest 从 DB 或向量库获取候选明星
 */
@Component
public class CandidateRetriever {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private PartnerMapper partnerMapper;

    @Autowired
    private MatchmakerRecommendationMapper recommendationMapper;

    public List<Partner> retrieve(MatchRequest request) {
        List<String> recommendedIds = getRecommendedIds(request.getSessionId());

        // 策略1：筛选型指代 → 只查已推荐
        if ("filter".equals(request.getRefType())) {
            if (recommendedIds.isEmpty()) return new ArrayList<>();
            return partnerMapper.selectByIds(recommendedIds);
        }

        // 策略2：有精确条件 → DB 全表过滤
        if (hasExactCondition(request)) {
            List<Partner> all = partnerMapper.selectByGender(request.getGender());
            return all.stream()
                    .filter(p -> matchExactCondition(p, request))
                    .filter(p -> !"exclude".equals(request.getRefType())
                            || !recommendedIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        // 策略3：无精确条件 → 向量检索
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder().query(request.getRawQuery()).topK(20).build()
        );
        List<String> ids = docs.stream()
                .filter(d -> request.getGender().equals(d.getMetadata().get("gender")))
                .map(d -> d.getMetadata().get("partnerId").toString())
                .filter(id -> !recommendedIds.contains(id) || !"exclude".equals(request.getRefType()))
                .toList();
        if (ids.isEmpty()) return new ArrayList<>();
        return partnerMapper.selectByIds(ids);
    }

    private List<String> getRecommendedIds(Long sessionId) {
        if (sessionId == null) return new ArrayList<>();
        List<MatchmakerRecommendation> recs = recommendationMapper.selectBySessionId(sessionId);
        return recs != null
                ? recs.stream().map(MatchmakerRecommendation::getPartnerId).toList()
                : new ArrayList<>();
    }

    private boolean hasExactCondition(MatchRequest request) {
        return request.getZodiac() != null
                || request.getAgeRange() != null
                || request.getNationality() != null;
    }

    private boolean matchExactCondition(Partner p, MatchRequest request) {
        if (request.getZodiac() != null
                && !request.getZodiac().equals(p.getZodiac())) {
            return false;
        }
        if (request.getNationality() != null
                && !request.getNationality().equals(p.getNationality())) {
            return false;
        }
        if (request.getAgeRange() != null && p.getAge() != null) {
            Integer[] range = request.getAgeRange();
            if (p.getAge() < range[0] || p.getAge() > range[1]) return false;
        }
        return true;
    }
}