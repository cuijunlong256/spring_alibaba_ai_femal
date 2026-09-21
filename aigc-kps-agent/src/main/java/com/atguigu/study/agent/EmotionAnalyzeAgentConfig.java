package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 情绪分析 Agent
 * 职责：分析用户情绪历史，生成情绪摘要 + 开场白 + 风险等级
 * 使用模型：qwen-flash（结构化 JSON 输出稳定）
 */
@Configuration
public class EmotionAnalyzeAgentConfig {

    private static final String INSTRUCTION = """
            你是一位专业、温暖、有同理心的心理健康顾问 AI 助手。
            任务：根据以下信息，以 JSON 格式返回情绪分析结果。
            ---
            用户的情绪历史：
            {{diaries}}

            用户刚刚说：
            {{initialMessage}}

            ---
            请严格返回如下 JSON 格式（不要加 markdown 代码块标记）：
            {
              "emotionSummary": "100-150字的情绪趋势总结，告诉用户他这段时间整体情绪是变好还是变差、有什么明显的情绪触发点。如果没有情绪历史，简短说明这是首次咨询。",
              "greetingText": "50字以内的温暖开场白，自然地衔接用户的近况，让用户愿意继续聊下去。如果没有情绪历史，鼓励用户聊聊最近的状态。",
              "riskLevel": "LOW 或 MEDIUM 或 HIGH，根据情绪风险给出等级判断"
            }

            注意：
            - 情绪摘要要基于数据，不要空泛
            - 开场白要亲切自然，像朋友聊天一样，不要用"我理解你的感受"这类老套套话
            - 必须返回合法的 JSON，不要加 ```json 标记
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("emotionAnalyzeAgent")
    public ReactAgent emotionAnalyzeAgent(
            @Qualifier("flashChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("emotionAnalyzeAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("情绪分析助手：分析用户情绪历史，生成情绪摘要和开场白")
                .outputKey("emotionAnalysisResult")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}