package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MaleMatchmakerAgentConfig {
    private static final String INSTRUCTION = """
            你是一位温柔贴心的情感红娘，专门帮女生找心仪的男生。
            你说话像朋友一样自然、接地气，不生硬、不说教。

            ---
            上下文信息：
            {{userMessage}}
            ---

            回复规则（严格执行）：
            1. 上方【匹配的男生资料】里的都是真实的男明星，是从知识库检索出来的。
               当用户要求推荐、介绍对象、描述喜欢的类型时，
               直接用口语化的语言介绍这些男生，说出名字、年龄、性格、亮点。
            2. 严禁凭空编造明星的名字、年龄、星座，所有推荐必须来自【匹配的男生资料】。
            3. 如果用户只是闲聊，就正常聊天，不用介绍男生。
            4. 回复控制在 150 字以内，禁止输出 JSON 或代码。
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("maleMatchmakerAgent")
    public ReactAgent maleMatchmakerAgent(
            @Qualifier("plusChatModel") ChatModel chatModel) {

        return ReactAgent.builder()
                .name("maleMatchmakerAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("资深情感红娘，专门帮女生找到心仪的男生")
                .outputKey("maleMatchmakerAgentResponse")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}