package com.atguigu.study.domain;

public class EmotionDiaryWithBLOBs extends EmotionDiary {
    //情绪触发
    private String emotionTriggers;

    //日记内容
    private String diaryContent;

    //ai情绪分析
    private String aiEmotionAnalysis;

    public String getEmotionTriggers() {
        return emotionTriggers;
    }

    public void setEmotionTriggers(String emotionTriggers) {
        this.emotionTriggers = emotionTriggers == null ? null : emotionTriggers.trim();
    }

    public String getDiaryContent() {
        return diaryContent;
    }

    public void setDiaryContent(String diaryContent) {
        this.diaryContent = diaryContent == null ? null : diaryContent.trim();
    }

    public String getAiEmotionAnalysis() {
        return aiEmotionAnalysis;
    }

    public void setAiEmotionAnalysis(String aiEmotionAnalysis) {
        this.aiEmotionAnalysis = aiEmotionAnalysis == null ? null : aiEmotionAnalysis.trim();
    }
}