package com.atguigu.study.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationSessionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConsultationSessionExample() {
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

        public Criteria andSessionTitleIsNull() {
            addCriterion("session_title is null");
            return (Criteria) this;
        }

        public Criteria andSessionTitleIsNotNull() {
            addCriterion("session_title is not null");
            return (Criteria) this;
        }

        public Criteria andSessionTitleEqualTo(String value) {
            addCriterion("session_title =", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleNotEqualTo(String value) {
            addCriterion("session_title <>", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleGreaterThan(String value) {
            addCriterion("session_title >", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleGreaterThanOrEqualTo(String value) {
            addCriterion("session_title >=", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleLessThan(String value) {
            addCriterion("session_title <", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleLessThanOrEqualTo(String value) {
            addCriterion("session_title <=", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleLike(String value) {
            addCriterion("session_title like", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleNotLike(String value) {
            addCriterion("session_title not like", value, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleIn(List<String> values) {
            addCriterion("session_title in", values, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleNotIn(List<String> values) {
            addCriterion("session_title not in", values, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleBetween(String value1, String value2) {
            addCriterion("session_title between", value1, value2, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andSessionTitleNotBetween(String value1, String value2) {
            addCriterion("session_title not between", value1, value2, "sessionTitle");
            return (Criteria) this;
        }

        public Criteria andStartedAtIsNull() {
            addCriterion("started_at is null");
            return (Criteria) this;
        }

        public Criteria andStartedAtIsNotNull() {
            addCriterion("started_at is not null");
            return (Criteria) this;
        }

        public Criteria andStartedAtEqualTo(LocalDateTime value) {
            addCriterion("started_at =", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtNotEqualTo(LocalDateTime value) {
            addCriterion("started_at <>", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtGreaterThan(LocalDateTime value) {
            addCriterion("started_at >", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("started_at >=", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtLessThan(LocalDateTime value) {
            addCriterion("started_at <", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("started_at <=", value, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtIn(List<LocalDateTime> values) {
            addCriterion("started_at in", values, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtNotIn(List<LocalDateTime> values) {
            addCriterion("started_at not in", values, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("started_at between", value1, value2, "startedAt");
            return (Criteria) this;
        }

        public Criteria andStartedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("started_at not between", value1, value2, "startedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisIsNull() {
            addCriterion("last_emotion_analysis is null");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisIsNotNull() {
            addCriterion("last_emotion_analysis is not null");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisEqualTo(String value) {
            addCriterion("last_emotion_analysis =", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisNotEqualTo(String value) {
            addCriterion("last_emotion_analysis <>", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisGreaterThan(String value) {
            addCriterion("last_emotion_analysis >", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisGreaterThanOrEqualTo(String value) {
            addCriterion("last_emotion_analysis >=", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisLessThan(String value) {
            addCriterion("last_emotion_analysis <", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisLessThanOrEqualTo(String value) {
            addCriterion("last_emotion_analysis <=", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisLike(String value) {
            addCriterion("last_emotion_analysis like", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisNotLike(String value) {
            addCriterion("last_emotion_analysis not like", value, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisIn(List<String> values) {
            addCriterion("last_emotion_analysis in", values, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisNotIn(List<String> values) {
            addCriterion("last_emotion_analysis not in", values, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisBetween(String value1, String value2) {
            addCriterion("last_emotion_analysis between", value1, value2, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionAnalysisNotBetween(String value1, String value2) {
            addCriterion("last_emotion_analysis not between", value1, value2, "lastEmotionAnalysis");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtIsNull() {
            addCriterion("last_emotion_updated_at is null");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtIsNotNull() {
            addCriterion("last_emotion_updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("last_emotion_updated_at =", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("last_emotion_updated_at <>", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("last_emotion_updated_at >", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("last_emotion_updated_at >=", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("last_emotion_updated_at <", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("last_emotion_updated_at <=", value, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("last_emotion_updated_at in", values, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("last_emotion_updated_at not in", values, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("last_emotion_updated_at between", value1, value2, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andLastEmotionUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("last_emotion_updated_at not between", value1, value2, "lastEmotionUpdatedAt");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
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