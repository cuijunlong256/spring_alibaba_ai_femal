package com.atguigu.study.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationSession {
    private Long id;

    private Long userId;

    private String sessionTitle;

    private LocalDateTime startedAt;

    private String lastEmotionAnalysis;

    private LocalDateTime lastEmotionUpdatedAt;

    private String type;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle == null ? null : sessionTitle.trim();
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public String getLastEmotionAnalysis() {
        return lastEmotionAnalysis;
    }

    public void setLastEmotionAnalysis(String lastEmotionAnalysis) {
        this.lastEmotionAnalysis = lastEmotionAnalysis == null ? null : lastEmotionAnalysis.trim();
    }

    public LocalDateTime getLastEmotionUpdatedAt() {
        return lastEmotionUpdatedAt;
    }

    public void setLastEmotionUpdatedAt(LocalDateTime lastEmotionUpdatedAt) {
        this.lastEmotionUpdatedAt = lastEmotionUpdatedAt;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}