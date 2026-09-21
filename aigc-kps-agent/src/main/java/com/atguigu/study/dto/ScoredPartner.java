package com.atguigu.study.dto;

import com.atguigu.study.domain.Partner;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * 带匹配度评分的明星
 */
@Data
@Builder
public class ScoredPartner {
    private Partner partner;
    /** 总分 0-100 */
    private double totalScore;
    /** 精确条件分 */
    private double exactScore;
    /** 标签匹配分 */
    private double tagScore;
    /** 向量相似度分 */
    private double vectorScore;
    /** 命中的标签 */
    private List<String> matchedTags;
}