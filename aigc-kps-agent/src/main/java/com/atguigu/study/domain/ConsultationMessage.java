package com.atguigu.study.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationMessage {
    private Long id;

    private Long sessionId;

    private Integer senderType;

    private Integer messageType;

    private String emotionTag;

    private String aiModel;

    private LocalDateTime createdAt;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSenderType() {
        return senderType;
    }

    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getEmotionTag() {
        return emotionTag;
    }

    public void setEmotionTag(String emotionTag) {
        this.emotionTag = emotionTag == null ? null : emotionTag.trim();
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel == null ? null : aiModel.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}