package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 风险评估 Agent
 * 职责：实时检测对话中的自杀/自伤倾向等高危信号
 */
@Configuration
public class RiskAssessmentAgentConfig {

    private static final String INSTRUCTION = """
            你是一个安全审核 Agent，只负责检测自杀、自伤等极端高危信号。
            
            **重要：** 只有当用户明确表达轻生、自伤意图时才判定为高风险。
            普通的心情不好、伤心、难过、压力大都属于 LOW，不要过度敏感。

            请分析以下用户消息，然后**只返回 JSON**，不要有任何其他文字：

            ---
            用户消息：
            {{userMessage}}
            ---

            只返回如下 JSON：
            {
              "hasRisk": true或false,
              "riskType": "SUICIDE" | "SELF_HARM" | "EXTREME_DISTRESS" | "NONE",
              "riskLevel": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL",
              "suggestion": "如果有风险填写建议，无风险填空字符串"
            }

            判断标准（严格执行）：
            - "LOW": 普通聊天、心情不好、伤心、难过、压力大、抱怨生活 —— 绝大多数情况都是 LOW
            - "MEDIUM": 用户明确表达迷茫、无望、不想继续、想逃避
            - "HIGH": 用户明确提到不想活、想消失、想结束一切、活着没意思
            - "CRITICAL": 用户有具体的自伤/自杀计划或正在实施

            **默认返回 LOW，除非有明确的高危信号。**
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("riskAssessmentAgent")
    public ReactAgent riskAssessmentAgent(
            @Qualifier("flashChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("riskAssessmentAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("风险评估助手：检测对话中的高危信号")
                .outputKey("riskAssessmentResult")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}