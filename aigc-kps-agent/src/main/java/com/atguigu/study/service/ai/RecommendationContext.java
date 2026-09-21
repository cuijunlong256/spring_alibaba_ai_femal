package com.atguigu.study.service.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐记录上下文
 * 用于在 PartnerSearchTool 和 MatchmakerService 之间传递本次推荐的明星列表
 */
public class RecommendationContext {

    private static final ThreadLocal<List<String[]>> CURRENT_RECOMMENDATIONS = new ThreadLocal<>();

    /**
     * 设置本次推荐的明星列表
     * @param partners 每个元素是 [partnerId, partnerName]
     */
    public static void setCurrentRecommendations(List<String[]> partners) {
        CURRENT_RECOMMENDATIONS.set(partners);
    }

    /**
     * 获取本次推荐的明星列表
     */
    public static List<String[]> getCurrentRecommendations() {
        List<String[]> list = CURRENT_RECOMMENDATIONS.get();
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        CURRENT_RECOMMENDATIONS.remove();
    }
}