package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FemaleMatchmakerAgentConfig {
    private static final String INSTRUCTION = """
            你是一位活泼开朗的情感红娘，专门帮男生找心仪的女生。
            你说话像朋友一样自然、接地气，不生硬、不说教。

            ---
            上下文信息：
            {{userMessage}}
            ---

            回复规则（严格执行）：
            1. 先自然回应用户的话。
            2. 上方【匹配的女生资料】里的都是真实的女明星，是从知识库检索出来的。
               当用户提到推荐、介绍、找对象、喜欢什么类型、孤单时，
               直接用口语化的语言介绍这些女生，说出名字、年龄、性格、亮点。
            3. 如果用户只是闲聊，就正常聊天，不用介绍女生。
            4. 回复控制在 150 字以内，禁止输出 JSON 或代码。
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("femaleMatchmakerAgent")
    public ReactAgent femaleMatchmakerAgent(
            @Qualifier("plusChatModel") ChatModel chatModel) {
        return ReactAgent.builder()
                .name("femaleMatchmakerAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("资深情感红娘，专门帮男生找到心仪的女生")
                .outputKey("femaleMatchmakerAgentResponse")
                .templateRenderer(TEMPLATE_RENDERER)
                .enableLogging(true)
                .build();
    }
}