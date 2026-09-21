package com.atguigu.study.skill;

import com.atguigu.study.domain.Partner;
import com.atguigu.study.dto.MatchRequest;
import com.atguigu.study.dto.ScoredPartner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 匹配度打分器：对候选明星按维度打分排序
 */
@Component
public class MatchScorer {

    /** 精确条件权重（星座/年龄/国籍） */
    private static final double WEIGHT_EXACT = 50.0;
    /** 标签匹配权重 */
    private static final double WEIGHT_TAG = 30.0;
    /** 向量相似度权重 */
    private static final double WEIGHT_VECTOR = 20.0;

    public List<ScoredPartner> score(List<Partner> candidates, MatchRequest request) {
        if (candidates == null || candidates.isEmpty()) {
            return new ArrayList<>();
        }
        return candidates.stream()
                .map(p -> scoreOne(p, request))
                .sorted(Comparator.comparingDouble(ScoredPartner::getTotalScore).reversed())
                .collect(Collectors.toList());
    }

    private ScoredPartner scoreOne(Partner p, MatchRequest request) {
        double exactScore = calcExactScore(p, request);
        TagResult tagResult = calcTagScore(p, request.getFuzzyTags());
        // 向量相似度分这里简化处理，实际可从检索结果带入 distance
        double vectorScore = 15.0;

        double total = exactScore * (WEIGHT_EXACT / 100)
                + tagResult.score * (WEIGHT_TAG / 100)
                + vectorScore * (WEIGHT_VECTOR / 100);

        return ScoredPartner.builder()
                .partner(p)
                .exactScore(exactScore)
                .tagScore(tagResult.score)
                .vectorScore(vectorScore)
                .totalScore(Math.min(total, 100))
                .matchedTags(tagResult.matchedTags)
                .build();
    }

    /** 精确条件打分：星座20 + 年龄15 + 国籍15 = 50 */
    private double calcExactScore(Partner p, MatchRequest request) {
        double score = 0;
        if (request.getZodiac() != null) {
            score += request.getZodiac().equals(p.getZodiac()) ? 20 : 0;
        } else {
            score += 20;
        }
        if (request.getAgeRange() != null && p.getAge() != null) {
            Integer[] range = request.getAgeRange();
            score += (p.getAge() >= range[0] && p.getAge() <= range[1]) ? 15 : 0;
        } else {
            score += 15;
        }
        if (request.getNationality() != null) {
            score += request.getNationality().equals(p.getNationality()) ? 15 : 0;
        } else {
            score += 15;
        }
        return score;
    }

    /** 标签匹配打分 */
    /** 标签匹配打分 */
    private TagResult calcTagScore(Partner p, List<String> fuzzyTags) {
        if (fuzzyTags == null || fuzzyTags.isEmpty()) {
            return new TagResult(30, new ArrayList<>());
        }
        List<String> matched = new ArrayList<>();

        // 拼接所有文本字段用于匹配
        StringBuilder profile = new StringBuilder();
        if (p.getPersonality() != null) profile.append(p.getPersonality());
        if (p.getTags() != null) profile.append(String.join(",", p.getTags()));
        if (p.getInterests() != null) profile.append(p.getInterests());

        String profileStr = profile.toString();
        for (String tag : fuzzyTags) {
            if (profileStr.contains(tag)) matched.add(tag);
        }
        double score = (double) matched.size() / fuzzyTags.size() * 30;
        return new TagResult(score, matched);
    }

    private static class TagResult {
        final double score;
        final List<String> matchedTags;
        TagResult(double score, List<String> matchedTags) {
            this.score = score;
            this.matchedTags = matchedTags;
        }
    }
}