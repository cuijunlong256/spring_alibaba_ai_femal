package com.atguigu.study.agent;

//import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 心理健康 AI 助手
 * 负责：情绪分析、情绪摘要生成、引导用户开启咨询
 *
 * 依赖：Spring AI Alibaba Starter 会自动装配 DashScopeChatModel
 */
//@Configuration
public class PsychologicalAgent {

    private final static String SYSTEM_PROMPT = """
            你是一位专业、温暖、有同理心的心理健康顾问 AI 助手。
              你的职责：
                 1. 分析用户提供的情绪日记数据，生成专业的情绪趋势总结
                 2. 用温和、不评判的语言和用户对话
                 3. 在用户没有情绪记录时，用引导性的问题帮助用户表达感受
                 4. 回复要简洁、自然，不要过于学术化
            
              回复要求：
                - 情绪摘要控制在 150 字以内
                - 引导语要亲切，像朋友聊天一样
                - 不要使用"我理解你的感受"这类老套的套话
            """;

//    @Bean
    public ReactAgent emotionAnalyzeAgent(org.springframework.ai.chat.model.ChatModel chatModel) {
        return ReactAgent.builder()
                .name("emotionAnalyzeAgent")
                .model(chatModel)
                .instruction("""
                        你是一位专业、温暖、有同理心的心理健康顾问 AI 助手。
                                                任务：根据以下信息，以 JSON 格式返回情绪分析结果。
                                                ---
                                                用户的情绪历史：
                                                {diaries}
                        
                                                用户刚刚说：
                                                {initialMessage}
                        
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
                                                - 如果 hasHistory 为 false，emotionSummary 要建议用户每天记录情绪日记
                                                - 必须返回合法的 JSON，不要加 ```json 标记
                        """)
                .description("情绪分析助手")
                .outputKey("emotionAnalysisResult")  // 输出写入 State 的 key
                .enableLogging(true)
                .build();
    }

    /**
     * 心理引导 Agent（后续对话阶段用）
     * 职责：基于情绪分析结果 + 历史对话，生成共情的心理引导
     */
//    @Bean
    public ReactAgent psychologicalGuideAgent(org.springframework.ai.chat.model.ChatModel chatModel) {
        return ReactAgent.builder()
                .name("psychologicalGuideAgent")
                .model(chatModel)
                .instruction("""
                        你是一位专业的心理疗愈 AI 助手。
                        请根据以下上下文，给出温暖、有共情、有建设性的回应。
                        用户情绪分析：
                          {emotionSummary}
                         当前对话历史：
                         {messages}
                        用户最新消息：
                         {userMessage}
                        要求：
                          - 先共情，再引导，最后可以给一个小建议
                          - 回复控制在 200 字以内
                          - 语气要像朋友，不要像医生
                          - 如果用户表达了强烈的负面情绪，可以先安抚""")
                .outputKey("guideResponse")
                .enableLogging(true)
                .build();
    }

    /**
     * 风险评估 Agent（检测自杀/自伤倾向等高危信号）
     * 职责：实时监控对话中的风险信号
     */
//    @Bean
    public ReactAgent riskAssessmentAgent(org.springframework.ai.chat.model.ChatModel chatModel) {
        return ReactAgent.builder()
                .name("riskAssessmentAgent")
                .model(chatModel)
                .instruction("""
                        你是一个安全审核 Agent，负责检测对话中是否存在自杀、自伤、极端负面情绪等高危信号。
                        
                        用户消息：
                        {userMessage}
                        
                        历史对话：
                        {messages}
                        
                        请以 JSON 格式返回：
                        {
                          "hasRisk": true或false,
                          "riskType": "SUICIDE" | "SELF_HARM" | "EXTREME_DISTRESS" | "NONE",
                          "riskLevel": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL",
                          "suggestion": "如果有风险，建议立即联系人工客服或拨打心理援助热线"
                        }""")
                .description("风险评估助手")
                .outputKey("riskAssessmentResult")
                .enableLogging(true)
                .build();
    }


}
