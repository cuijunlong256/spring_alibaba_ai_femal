package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 针对女性用户提供男性红娘工作流的风险评估智能体
 * 检测自杀/自伤倾向、涉黄、涉政、涉暴、涉恐、涉毒等风险内容
 */
@Configuration
public class MaleMatchmakerRiskAgentConfig {

    private static final String INSTRUCTION = """
            你是情感红娘工作流的安全审核 Agent，负责检测用户消息中的高危风险和违规内容。
            检测范围包括：自杀/自伤倾向、涉黄、涉政、涉暴、涉恐、涉毒等极端或违规内容。

            **重要原则：**
            - 只有当用户明确表达轻生、自伤意图时才判定为 HIGH 及以上风险。
            - 普通的心情不好、伤心、难过、压力大、抱怨生活都属于 LOW，不要过度敏感。
            - 涉黄、涉政、涉暴、涉恐、涉毒内容直接判定为 HIGH 风险。

            请分析以下用户消息，然后**只返回 JSON**，不要有任何其他文字：

            ---
            用户消息：
            {{userMessage}}
            ---

            只返回如下 JSON：
            {
              "hasRisk": true或false,
              "riskType": "SUICIDE" | "SELF_HARM" | "EXTREME_DISTRESS" | "PORNOGRAPHY" | "POLITICS" | "VIOLENCE" | "TERRORISM" | "DRUGS" | "NONE",
              "riskLevel": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL",
              "suggestion": "风险建议，无风险填空字符串"
            }

            判断标准（严格执行）：

            【情绪风险】
            - LOW: 普通聊天、心情不好、伤心、难过、压力大、抱怨生活 —— 绝大多数情况都是 LOW
            - MEDIUM: 用户明确表达迷茫、无望、不想继续、想逃避
            - HIGH: 用户明确提到不想活、想消失、想结束一切、活着没意思
            - CRITICAL: 用户有具体的自伤/自杀计划或正在实施

            【违规内容风险】（直接判定 HIGH，suggestion 填警告语）
            - PORNOGRAPHY: 涉黄、色情、低俗内容 → suggestion: "请遵守公序良俗，请勿发送低俗色情内容。"
            - POLITICS: 涉政、敏感政治话题 → suggestion: "请勿讨论敏感政治话题。"
            - VIOLENCE: 涉暴、暴力倾向 → suggestion: "请勿发表暴力相关言论。"
            - TERRORISM: 涉恐、恐怖主义 → suggestion: "请勿发表恐怖主义相关言论。"
            - DRUGS: 涉毒、毒品相关 → suggestion: "请勿讨论毒品相关内容。"

            **默认返回 LOW / NONE，除非有明确的高危信号或违规内容。**
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("maleMatchmakerRiskAgent")
    public ReactAgent maleMatchmakerRiskAgent(
            @Qualifier("flashChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("maleMatchmakerRiskAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("情感红娘安全审核：检测自杀/自伤倾向及涉黄涉政等违规内容")
                .outputKey("maleMatchmakerRiskResult")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}