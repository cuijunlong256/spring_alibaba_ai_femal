package com.atguigu.study.skill;

import com.atguigu.study.domain.Partner;
import com.atguigu.study.dto.MatchRequest;
import com.atguigu.study.dto.ScoredPartner;
import com.atguigu.study.service.ai.RecommendationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 结果组装器：将打分后的明星列表格式化为文本，并更新推荐上下文
 */
@Component
public class ResultFormatter {

    public String format(List<ScoredPartner> scored, MatchRequest request) {
        if (scored == null || scored.isEmpty()) {
            return buildEmptyMessage(request);
        }

        // 取 top5
        List<ScoredPartner> top5 = scored.stream().limit(5).collect(Collectors.toList());

        // 非筛选型指代时，更新推荐上下文（供下一轮指代使用）
        if (!"filter".equals(request.getRefType())) {
            List<String[]> idNames = top5.stream()
                    .map(sp -> new String[]{sp.getPartner().getId(), sp.getPartner().getName()})
                    .collect(Collectors.toList());
            RecommendationContext.setCurrentRecommendations(idNames);
        }

        // 组装文本
        return top5.stream()
                .map(this::formatOne)
                .collect(Collectors.joining("\n\n"));
    }

    private String formatOne(ScoredPartner sp) {
        Partner p = sp.getPartner();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("【%s】匹配度 %.0f%%\n", p.getName(), sp.getTotalScore()));
        if (p.getAge() != null) sb.append("年龄：").append(p.getAge()).append("岁 ");
        if (p.getZodiac() != null) sb.append("星座：").append(p.getZodiac()).append(" ");
        if (p.getNationality() != null) sb.append("国籍：").append(p.getNationality());
        sb.append("\n");
        if (p.getPersonality() != null) sb.append("性格：").append(p.getPersonality()).append("\n");
        if (p.getInterests() != null) sb.append("爱好：").append(p.getInterests()).append("\n");
        if (!sp.getMatchedTags().isEmpty()) {
            sb.append("匹配点：").append(String.join("、", sp.getMatchedTags()));
        }
        return sb.toString();
    }

    private String buildEmptyMessage(MatchRequest request) {
        if ("exclude".equals(request.getRefType())) {
            return "已经把合适的都推荐给你啦，暂时没有更多啦。可以告诉我你喜欢什么类型，我再帮你找找~";
        }
        if ("filter".equals(request.getRefType())) {
            return "在之前推荐的人里，没有找到符合条件的。要不要换个条件试试？";
        }
        return "暂时没有找到完全符合条件的，要不要放宽一些条件？比如不限制星座或年龄~";
    }
}