package com.atguigu.study.domain;

public class KnowledgeArticleWithBLOBs extends KnowledgeArticle {
    private String summary;

    private String content;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}