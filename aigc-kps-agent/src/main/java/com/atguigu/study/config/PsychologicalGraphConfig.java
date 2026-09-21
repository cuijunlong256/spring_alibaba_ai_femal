package com.atguigu.study.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

@Configuration
public class PsychologicalGraphConfig {

    private static final Logger log = LoggerFactory.getLogger(PsychologicalGraphConfig.class);

    private static Map<String, Object> humanReviewNodeAction(OverAllState state) throws GraphStateException {
        Object riskResult = state.value("riskAssessmentResult").orElse(null);
        String suggestion = "";
        if (riskResult instanceof AssistantMessage am) {
            suggestion = am.getText();
        }
        if (suggestion == null || suggestion.isBlank()) {
            suggestion = "检测到高风险信号";
        }
        String emergencyReply = """
                我注意到你可能正处于非常困难的时刻。你的感受是真实的、重要的，请一定不要独自承受。

                如果有自伤或轻生的念头，请立即拨打以下心理援助热线：
                  全国心理援助热线：400-161-9995
                  北京心理危机研究与干预中心：010-82951332
                  紧急情况请拨打 120 或 110

                我会一直在这里陪你聊聊，但请优先联系专业人员的帮助。你值得被照顾。
                """;
        Map<String, Object> result = new HashMap<>();
        result.put("finalResponse", emergencyReply);
        result.put("isEmergency", true);
        return result;
    }

    /**
     * 从 riskAssessmentAgent 的输出中解析 riskLevel
     */
    private static String parseRiskLevel(OverAllState state) {
        Object riskResult = state.value("riskAssessmentResult").orElse(null);

        if (riskResult == null) {
            return "LOW";
        }

        String text;
        if (riskResult instanceof AssistantMessage am) {
            text = am.getText();
        } else {
            text = riskResult.toString();
        }

        try {
            int idx = text.indexOf("\"riskLevel\"");
            if (idx >= 0) {
                int colon = text.indexOf(':', idx);
                int quote1 = text.indexOf('"', colon + 1);
                int quote2 = text.indexOf('"', quote1 + 1);
                String level = text.substring(quote1 + 1, quote2).trim();
                log.info("风险评估解析结果: riskLevel={}", level);
                return level;
            }
        } catch (Exception e) {
            log.warn("风险评估解析失败，兜底返回 LOW: {}", e.getMessage());
        }

        return "LOW";
    }

    @Bean("compiledPsychStreamGraph")
    public CompiledGraph compiledPsychStreamGraph(
            @Qualifier("riskAssessmentAgent") ReactAgent riskAssessmentAgent,
            @Qualifier("psychologicalGuideAgent") ReactAgent psychologicalGuideAgent) throws GraphStateException {

        KeyStrategyFactory keyFactory = () -> {
            Map<String, KeyStrategy> key = new HashMap<>();
            key.put("userMessage", new ReplaceStrategy());
            key.put("riskAssessmentResult", new ReplaceStrategy());
            key.put("guideResponse", new ReplaceStrategy());
            key.put("finalResponse", new ReplaceStrategy());
            key.put("isEmergency", new ReplaceStrategy());
            return key;
        };

        StateGraph graph = new StateGraph("psychological_stream", keyFactory);

        graph.addNode("riskAssessmentAgent", riskAssessmentAgent.asNode());
        graph.addNode("psychologicalGuideAgent", psychologicalGuideAgent.asNode());
        graph.addNode("human_review", node_async(PsychologicalGraphConfig::humanReviewNodeAction));

        graph.addEdge(START, "riskAssessmentAgent");

        // 条件边：只有 HIGH 和 CRITICAL 才走人工审核，LOW/MEDIUM 走心理引导
        graph.addConditionalEdges("riskAssessmentAgent",
                edge_async(state -> {
                    String level = parseRiskLevel(state);
                    boolean isEmergency = "CRITICAL".equalsIgnoreCase(level)
                            || "HIGH".equalsIgnoreCase(level);
                    log.info("风险等级={} → 走{}", level, isEmergency ? "人工审核" : "心理引导");
                    return isEmergency ? "risk" : "safe";
                }),
                Map.of(
                        "risk", "human_review",
                        "safe", "psychologicalGuideAgent"
                ));

        graph.addEdge("human_review", END);
        graph.addEdge("psychologicalGuideAgent", END);

        return graph.compile();
    }

}