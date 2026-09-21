package com.atguigu.study.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.agent.renderer.SaaStTemplateRenderer;

import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import com.atguigu.study.skill.CelebrityMatchTool;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
            1. 当用户要求推荐、介绍对象、描述喜欢的类型（年龄/星座/性格等）、
               或说"换一批""还有其他的吗"时，必须先调用 celebrityMatchTool 工具
               检索真实明星资料，再根据返回结果用口语化语言介绍。
            2. 严禁凭空编造明星的名字、年龄、星座等信息，所有推荐必须来自工具返回结果。
            3. 如果用户只是闲聊（不涉及推荐），就正常聊天，不用调用工具。
            4. 回复控制在 150 字以内，禁止输出 JSON 或代码。
            """;

    private static final SaaStTemplateRenderer TEMPLATE_RENDERER = SaaStTemplateRenderer.builder()
            .startDelimiter("{{")
            .endDelimiter("}}")
            .build();

    @Bean
    @Qualifier("femaleMatchmakerAgent")
    public ReactAgent femaleMatchmakerAgent(
            @Qualifier("plusChatModel") ChatModel chatModel,
            CelebrityMatchTool celebrityMatchTool) {

        // 加载 skills 目录下的 SKILL.md
        SkillRegistry registry = ClasspathSkillRegistry.builder()
                .classpathPath("skills")
                .build();
        SkillsAgentHook skillsHook = SkillsAgentHook.builder()
                .skillRegistry(registry)
                .build();

        // 注册 @Tool 方法，获取 ToolCallback 列表
        ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(celebrityMatchTool)
                .build();

        return ReactAgent.builder()
                .name("femaleMatchmakerAgent")
                .model(chatModel)
                .instruction(INSTRUCTION)
                .description("资深情感红娘，专门帮男生找到心仪的女生")
                .outputKey("femaleMatchmakerAgentResponse")
                .templateRenderer(TEMPLATE_RENDERER)
                .tools(toolProvider.getToolCallbacks())
                .hooks(List.of(skillsHook))
                .enableLogging(true)
                .build();
    }
}