package com.atguigu.study.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationMessageExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ConsultationMessageExample() {
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

        public Criteria andSessionIdIsNull() {
            addCriterion("session_id is null");
            return (Criteria) this;
        }

        public Criteria andSessionIdIsNotNull() {
            addCriterion("session_id is not null");
            return (Criteria) this;
        }

        public Criteria andSessionIdEqualTo(Long value) {
            addCriterion("session_id =", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotEqualTo(Long value) {
            addCriterion("session_id <>", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdGreaterThan(Long value) {
            addCriterion("session_id >", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("session_id >=", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdLessThan(Long value) {
            addCriterion("session_id <", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdLessThanOrEqualTo(Long value) {
            addCriterion("session_id <=", value, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdIn(List<Long> values) {
            addCriterion("session_id in", values, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotIn(List<Long> values) {
            addCriterion("session_id not in", values, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdBetween(Long value1, Long value2) {
            addCriterion("session_id between", value1, value2, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSessionIdNotBetween(Long value1, Long value2) {
            addCriterion("session_id not between", value1, value2, "sessionId");
            return (Criteria) this;
        }

        public Criteria andSenderTypeIsNull() {
            addCriterion("sender_type is null");
            return (Criteria) this;
        }

        public Criteria andSenderTypeIsNotNull() {
            addCriterion("sender_type is not null");
            return (Criteria) this;
        }

        public Criteria andSenderTypeEqualTo(Integer value) {
            addCriterion("sender_type =", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeNotEqualTo(Integer value) {
            addCriterion("sender_type <>", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeGreaterThan(Integer value) {
            addCriterion("sender_type >", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("sender_type >=", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeLessThan(Integer value) {
            addCriterion("sender_type <", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeLessThanOrEqualTo(Integer value) {
            addCriterion("sender_type <=", value, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeIn(List<Integer> values) {
            addCriterion("sender_type in", values, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeNotIn(List<Integer> values) {
            addCriterion("sender_type not in", values, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeBetween(Integer value1, Integer value2) {
            addCriterion("sender_type between", value1, value2, "senderType");
            return (Criteria) this;
        }

        public Criteria andSenderTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("sender_type not between", value1, value2, "senderType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeIsNull() {
            addCriterion("message_type is null");
            return (Criteria) this;
        }

        public Criteria andMessageTypeIsNotNull() {
            addCriterion("message_type is not null");
            return (Criteria) this;
        }

        public Criteria andMessageTypeEqualTo(Integer value) {
            addCriterion("message_type =", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeNotEqualTo(Integer value) {
            addCriterion("message_type <>", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeGreaterThan(Integer value) {
            addCriterion("message_type >", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("message_type >=", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeLessThan(Integer value) {
            addCriterion("message_type <", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeLessThanOrEqualTo(Integer value) {
            addCriterion("message_type <=", value, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeIn(List<Integer> values) {
            addCriterion("message_type in", values, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeNotIn(List<Integer> values) {
            addCriterion("message_type not in", values, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeBetween(Integer value1, Integer value2) {
            addCriterion("message_type between", value1, value2, "messageType");
            return (Criteria) this;
        }

        public Criteria andMessageTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("message_type not between", value1, value2, "messageType");
            return (Criteria) this;
        }

        public Criteria andEmotionTagIsNull() {
            addCriterion("emotion_tag is null");
            return (Criteria) this;
        }

        public Criteria andEmotionTagIsNotNull() {
            addCriterion("emotion_tag is not null");
            return (Criteria) this;
        }

        public Criteria andEmotionTagEqualTo(String value) {
            addCriterion("emotion_tag =", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagNotEqualTo(String value) {
            addCriterion("emotion_tag <>", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagGreaterThan(String value) {
            addCriterion("emotion_tag >", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagGreaterThanOrEqualTo(String value) {
            addCriterion("emotion_tag >=", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagLessThan(String value) {
            addCriterion("emotion_tag <", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagLessThanOrEqualTo(String value) {
            addCriterion("emotion_tag <=", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagLike(String value) {
            addCriterion("emotion_tag like", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagNotLike(String value) {
            addCriterion("emotion_tag not like", value, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagIn(List<String> values) {
            addCriterion("emotion_tag in", values, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagNotIn(List<String> values) {
            addCriterion("emotion_tag not in", values, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagBetween(String value1, String value2) {
            addCriterion("emotion_tag between", value1, value2, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andEmotionTagNotBetween(String value1, String value2) {
            addCriterion("emotion_tag not between", value1, value2, "emotionTag");
            return (Criteria) this;
        }

        public Criteria andAiModelIsNull() {
            addCriterion("ai_model is null");
            return (Criteria) this;
        }

        public Criteria andAiModelIsNotNull() {
            addCriterion("ai_model is not null");
            return (Criteria) this;
        }

        public Criteria andAiModelEqualTo(String value) {
            addCriterion("ai_model =", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelNotEqualTo(String value) {
            addCriterion("ai_model <>", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelGreaterThan(String value) {
            addCriterion("ai_model >", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelGreaterThanOrEqualTo(String value) {
            addCriterion("ai_model >=", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelLessThan(String value) {
            addCriterion("ai_model <", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelLessThanOrEqualTo(String value) {
            addCriterion("ai_model <=", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelLike(String value) {
            addCriterion("ai_model like", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelNotLike(String value) {
            addCriterion("ai_model not like", value, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelIn(List<String> values) {
            addCriterion("ai_model in", values, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelNotIn(List<String> values) {
            addCriterion("ai_model not in", values, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelBetween(String value1, String value2) {
            addCriterion("ai_model between", value1, value2, "aiModel");
            return (Criteria) this;
        }

        public Criteria andAiModelNotBetween(String value1, String value2) {
            addCriterion("ai_model not between", value1, value2, "aiModel");
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