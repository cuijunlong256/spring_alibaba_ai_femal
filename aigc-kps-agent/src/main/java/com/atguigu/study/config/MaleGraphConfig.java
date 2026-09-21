package com.atguigu.study.config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * 针对女性用户提供男性红娘工作流
 */
@Configuration
public class MaleGraphConfig {

    private static final Logger log = LoggerFactory.getLogger(MaleGraphConfig.class);

    @Bean("MaleMatchmakerGraph")
    public CompiledGraph MaleMatchmakerGraph(
            @Qualifier("maleMatchmakerAgent") ReactAgent maleMatchmakerAgent,
            @Qualifier("maleMatchmakerRiskAgent") ReactAgent maleMatchmakerRiskAgent) throws GraphStateException {

        KeyStrategyFactory keyFactory = () -> {
            Map<String, KeyStrategy> key = new HashMap<>();
            key.put("userMessage", new ReplaceStrategy());
            key.put("maleMatchmakerAgentResponse", new ReplaceStrategy());
            key.put("maleMatchmakerRiskResult", new ReplaceStrategy());
            key.put("finalResponse", new ReplaceStrategy());
            key.put("isEmergency", new ReplaceStrategy());
            return key;
        };

        StateGraph graph = new StateGraph("maleMatchmaker_stream", keyFactory);

        graph.addNode("maleMatchmakerAgent", maleMatchmakerAgent.asNode());
        graph.addNode("maleMatchmakerRiskAgent", maleMatchmakerRiskAgent.asNode());
        graph.addNode("human_review", node_async(MaleGraphConfig::humanReviewNodeAction));

        graph.addEdge(START, "maleMatchmakerRiskAgent");

        graph.addConditionalEdges("maleMatchmakerRiskAgent",
                edge_async(state -> {
                    String level = parseRiskLevel(state);
                    boolean isEmergency = "CRITICAL".equalsIgnoreCase(level)
                            || "HIGH".equalsIgnoreCase(level);
                    log.info("风险等级={} → 走{}", level, isEmergency ? "风险处理" : "红娘推荐");
                    return isEmergency ? "risk" : "safe";
                }),
                Map.of(
                        "risk", "human_review",
                        "safe", "maleMatchmakerAgent"
                ));

        graph.addEdge("human_review", END);
        graph.addEdge("maleMatchmakerAgent", END);

        return graph.compile();
    }

    private static String parseRiskLevel(OverAllState state) {
        Object riskResult = state.value("maleMatchmakerRiskResult").orElse(null);

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

    private static Map<String, Object> humanReviewNodeAction(OverAllState state) throws GraphStateException {
        Object riskResult = state.value("maleMatchmakerRiskResult").orElse(null);
        String text = "";
        if (riskResult instanceof AssistantMessage am) {
            text = am.getText();
        }

        String riskType = parseJsonField(text, "riskType");
        log.info("human_review 解析到 riskType={}", riskType);

        String reply;

        switch (riskType) {
            case "SUICIDE":
            case "SELF_HARM":
            case "EXTREME_DISTRESS":
                reply = """
                        我注意到你可能正处于非常困难的时刻。你的感受是真实的、重要的，请一定不要独自承受。

                        如果有自伤或轻生的念头，请立即拨打以下心理援助热线：
                          全国心理援助热线：400-161-9995
                          北京心理危机研究与干预中心：010-82951332
                          紧急情况请拨打 120 或 110

                        我会一直在这里陪你聊聊，但请优先联系专业人员的帮助。你值得被照顾。
                        """;
                break;
            case "PORNOGRAPHY":
                reply = "请遵守公序良俗，请勿发送低俗色情内容。";
                break;
            case "POLITICS":
                reply = "请勿讨论敏感政治话题。";
                break;
            case "VIOLENCE":
                reply = "请勿发表暴力相关言论。";
                break;
            case "TERRORISM":
                reply = "请勿发表恐怖主义相关言论。";
                break;
            case "DRUGS":
                reply = "请勿讨论毒品相关内容。";
                break;
            default:
                reply = "检测到高风险信号，请规范你的发言。";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("finalResponse", reply);
        result.put("isEmergency", true);
        return result;
    }

    private static String parseJsonField(String text, String field) {
        if (text == null || text.isBlank()) return "NONE";
        try {
            String key = "\"" + field + "\"";
            int idx = text.indexOf(key);
            if (idx < 0) return "NONE";
            int colon = text.indexOf(':', idx);
            int quote1 = text.indexOf('"', colon + 1);
            int quote2 = text.indexOf('"', quote1 + 1);
            return text.substring(quote1 + 1, quote2).trim();
        } catch (Exception e) {
            return "NONE";
        }
    }
}