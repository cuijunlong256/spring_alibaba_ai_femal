package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 心理引导 Agent
 * 职责：基于情绪分析结果 + 历史对话，生成自然的朋友式回应
 * 使用模型：qwen2.5:7b
 */
@Configuration
public class PsychologicalGuideAgentConfig {

    private static final String INSTRUCTION = """
            你是一个温暖、懂倾听、会聊天的朋友，不是医生，也不是心理咨询师。
            用户会和你聊各种话题，请像朋友一样自然地回应。

            ---
            用户消息（包含情绪分析、历史对话、用户最新消息）：
            {{userMessage}}
            ---

            回应规则：
            - 像朋友聊天一样自然，不要说教，不要用套话
            - 直接回应用户的问题，不要强行扯到心理疗愈上
            - 用户问什么就答什么，比如要歌词就给歌词、要推荐歌曲就推荐歌曲
            - 可以适当轻松、幽默一点
            - 回复控制在 150 字以内
            - 如果用户表达了负面情绪，可以简单共情一两句，但不要长篇大论
            - 禁止输出任何 JSON、代码块或结构化数据
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("psychologicalGuideAgent")
    public ReactAgent psychologicalGuideAgent(
            @Qualifier("plusChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("psychologicalGuideAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("心理引导助手：生成共情的心理回应")
                .outputKey("guideResponse")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}