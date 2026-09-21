package com.atguigu.study.skill;

import com.atguigu.study.domain.Partner;
import com.atguigu.study.dto.MatchRequest;
import com.atguigu.study.dto.ScoredPartner;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 明星画像深度匹配技能入口
 * 模型通过 Function Calling 调用此工具，内部走 解析→检索→打分→组装 流水线
 */
@Component
public class CelebrityMatchTool {

    @Autowired
    private IntentParser intentParser;

    @Autowired
    private CandidateRetriever candidateRetriever;

    @Autowired
    private MatchScorer matchScorer;

    @Autowired
    private ResultFormatter resultFormatter;

    @Tool(description = "明星画像深度匹配：根据用户描述（年龄、星座、国籍、性格标签等）推荐最匹配的明星。" +
            "query 是用户描述，gender 是目标性别(female/male)，sessionId 是会话ID")
    public String celebrityMatch(String query, String gender, Long sessionId) {
        // Step1: 解析意图
        MatchRequest request = intentParser.parse(query, gender, sessionId);

        // Step2: 检索候选集
        List<Partner> candidates = candidateRetriever.retrieve(request);

        // Step3: 打分排序
        List<ScoredPartner> scored = matchScorer.score(candidates, request);

        // Step4: 组装结果
        return resultFormatter.format(scored, request);
    }
}