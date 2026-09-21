package com.atguigu.study.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 明星匹配请求（意图解析后的结构化条件）
 */
@Data
@Builder
public class MatchRequest {
    /** 目标性别 female/male */
    private String gender;
    /** 年龄范围 [min, max] */
    private Integer[] ageRange;
    /** 星座 */
    private String zodiac;
    /** 国籍 */
    private String nationality;
    /** 模糊标签（知性、文艺等） */
    private List<String> fuzzyTags;
    /** 指代类型：exclude（排除已推荐）/ filter（在已推荐中筛选）/ null（无指代） */
    private String refType;
    /** 会话ID */
    private Long sessionId;
    /** 原始用户输入（用于向量检索） */
    private String rawQuery;
}