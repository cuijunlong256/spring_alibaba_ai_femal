package com.atguigu.study.service;

import java.time.LocalDate;
import java.util.List;

public class StructOutPut {
    public record StreamChatSession(
            String sessionId,
            Long userId,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            Integer messageCount,
            String status,

            // ✅ 新增：情绪历史相关
            EmotionTrend emotionTrend,       // 最近 7 天情绪趋势（用于前端展示图表）
            String emotionSummary,            // AI 生成的情绪摘要（用于 StateGraph 初始 State）
            String greetingText,              // 打招呼文本（用于 StateGraph 初始 State）
            List<EmotionDiarySummary> recentDiaries  // 最近几条日记摘要
    ) {}

    // 辅助 record
    public record EmotionTrend(
            Integer avgMoodScore,             // 近 7 天平均分
            Integer avgSleepQuality,          // 近 7 天平均睡眠
            Integer avgStressLevel,           // 近 7 天平均压力
            String trendDirection,            // "UP"/"DOWN"/"STABLE"
            List<DailyMood> dailyMoods        // 每天的心情分数（用于折线图）
    ) {}

    public record DailyMood(
            LocalDate date,
            Integer moodScore,
            String dominantEmotion
    ) {}

    public record EmotionDiarySummary(
            LocalDate date,
            Integer moodScore,
            String dominantEmotion,
            String aiEmotionAnalysis          // 每天的 AI 分析
    ) {}
}
