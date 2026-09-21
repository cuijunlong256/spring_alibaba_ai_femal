package com.atguigu.study.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmotionDiaryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmotionDiaryExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andDiaryDateIsNull() {
            addCriterion("diary_date is null");
            return (Criteria) this;
        }

        public Criteria andDiaryDateIsNotNull() {
            addCriterion("diary_date is not null");
            return (Criteria) this;
        }

        public Criteria andDiaryDateEqualTo(LocalDate value) {
            addCriterion("diary_date =", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateNotEqualTo(LocalDate value) {
            addCriterion("diary_date <>", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateGreaterThan(LocalDate value) {
            addCriterion("diary_date >", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("diary_date >=", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateLessThan(LocalDate value) {
            addCriterion("diary_date <", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateLessThanOrEqualTo(LocalDate value) {
            addCriterion("diary_date <=", value, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateIn(List<LocalDate> values) {
            addCriterion("diary_date in", values, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateNotIn(List<LocalDate> values) {
            addCriterion("diary_date not in", values, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateBetween(LocalDate value1, LocalDate value2) {
            addCriterion("diary_date between", value1, value2, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andDiaryDateNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("diary_date not between", value1, value2, "diaryDate");
            return (Criteria) this;
        }

        public Criteria andMoodScoreIsNull() {
            addCriterion("mood_score is null");
            return (Criteria) this;
        }

        public Criteria andMoodScoreIsNotNull() {
            addCriterion("mood_score is not null");
            return (Criteria) this;
        }

        public Criteria andMoodScoreEqualTo(Integer value) {
            addCriterion("mood_score =", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreNotEqualTo(Integer value) {
            addCriterion("mood_score <>", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreGreaterThan(Integer value) {
            addCriterion("mood_score >", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("mood_score >=", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreLessThan(Integer value) {
            addCriterion("mood_score <", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreLessThanOrEqualTo(Integer value) {
            addCriterion("mood_score <=", value, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreIn(List<Integer> values) {
            addCriterion("mood_score in", values, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreNotIn(List<Integer> values) {
            addCriterion("mood_score not in", values, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreBetween(Integer value1, Integer value2) {
            addCriterion("mood_score between", value1, value2, "moodScore");
            return (Criteria) this;
        }

        public Criteria andMoodScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("mood_score not between", value1, value2, "moodScore");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionIsNull() {
            addCriterion("dominant_emotion is null");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionIsNotNull() {
            addCriterion("dominant_emotion is not null");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionEqualTo(String value) {
            addCriterion("dominant_emotion =", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionNotEqualTo(String value) {
            addCriterion("dominant_emotion <>", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionGreaterThan(String value) {
            addCriterion("dominant_emotion >", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionGreaterThanOrEqualTo(String value) {
            addCriterion("dominant_emotion >=", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionLessThan(String value) {
            addCriterion("dominant_emotion <", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionLessThanOrEqualTo(String value) {
            addCriterion("dominant_emotion <=", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionLike(String value) {
            addCriterion("dominant_emotion like", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionNotLike(String value) {
            addCriterion("dominant_emotion not like", value, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionIn(List<String> values) {
            addCriterion("dominant_emotion in", values, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionNotIn(List<String> values) {
            addCriterion("dominant_emotion not in", values, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionBetween(String value1, String value2) {
            addCriterion("dominant_emotion between", value1, value2, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andDominantEmotionNotBetween(String value1, String value2) {
            addCriterion("dominant_emotion not between", value1, value2, "dominantEmotion");
            return (Criteria) this;
        }

        public Criteria andSleepQualityIsNull() {
            addCriterion("sleep_quality is null");
            return (Criteria) this;
        }

        public Criteria andSleepQualityIsNotNull() {
            addCriterion("sleep_quality is not null");
            return (Criteria) this;
        }

        public Criteria andSleepQualityEqualTo(Integer value) {
            addCriterion("sleep_quality =", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityNotEqualTo(Integer value) {
            addCriterion("sleep_quality <>", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityGreaterThan(Integer value) {
            addCriterion("sleep_quality >", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityGreaterThanOrEqualTo(Integer value) {
            addCriterion("sleep_quality >=", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityLessThan(Integer value) {
            addCriterion("sleep_quality <", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityLessThanOrEqualTo(Integer value) {
            addCriterion("sleep_quality <=", value, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityIn(List<Integer> values) {
            addCriterion("sleep_quality in", values, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityNotIn(List<Integer> values) {
            addCriterion("sleep_quality not in", values, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityBetween(Integer value1, Integer value2) {
            addCriterion("sleep_quality between", value1, value2, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andSleepQualityNotBetween(Integer value1, Integer value2) {
            addCriterion("sleep_quality not between", value1, value2, "sleepQuality");
            return (Criteria) this;
        }

        public Criteria andStressLevelIsNull() {
            addCriterion("stress_level is null");
            return (Criteria) this;
        }

        public Criteria andStressLevelIsNotNull() {
            addCriterion("stress_level is not null");
            return (Criteria) this;
        }

        public Criteria andStressLevelEqualTo(Integer value) {
            addCriterion("stress_level =", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelNotEqualTo(Integer value) {
            addCriterion("stress_level <>", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelGreaterThan(Integer value) {
            addCriterion("stress_level >", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelGreaterThanOrEqualTo(Integer value) {
            addCriterion("stress_level >=", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelLessThan(Integer value) {
            addCriterion("stress_level <", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelLessThanOrEqualTo(Integer value) {
            addCriterion("stress_level <=", value, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelIn(List<Integer> values) {
            addCriterion("stress_level in", values, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelNotIn(List<Integer> values) {
            addCriterion("stress_level not in", values, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelBetween(Integer value1, Integer value2) {
            addCriterion("stress_level between", value1, value2, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andStressLevelNotBetween(Integer value1, Integer value2) {
            addCriterion("stress_level not between", value1, value2, "stressLevel");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtIsNull() {
            addCriterion("ai_analysis_updated_at is null");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtIsNotNull() {
            addCriterion("ai_analysis_updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at =", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at <>", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at >", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at >=", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at <", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("ai_analysis_updated_at <=", value, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("ai_analysis_updated_at in", values, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("ai_analysis_updated_at not in", values, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("ai_analysis_updated_at between", value1, value2, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andAiAnalysisUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("ai_analysis_updated_at not between", value1, value2, "aiAnalysisUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(LocalDateTime value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(LocalDateTime value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(LocalDateTime value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<LocalDateTime> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}