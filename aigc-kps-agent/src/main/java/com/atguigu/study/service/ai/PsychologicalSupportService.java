package com.atguigu.study.service.ai;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.GraphResponse;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.domain.EmotionDiaryWithBLOBs;
import com.atguigu.study.dto.command.ConsultationSessionCreateDTO;
import com.atguigu.study.service.EmotionDiaryService;
import com.atguigu.study.service.StructOutPut;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PsychologicalSupportService {

    private final static Logger log = LoggerFactory.getLogger(PsychologicalSupportService.class);

    @Autowired
    private ConsultationSessionService consultationSessionService;

    @Autowired
    private ConsultationMessageService consultationMessageService;

    @Autowired
    private EmotionDiaryService emotionDiaryService;

    // ✅ 正确：注入 ReactAgent + @Qualifier 指定 Bean 名
    @Autowired
    @Qualifier("emotionAnalyzeAgent")
    private ReactAgent emotionAnalyzeAgent;

    @Autowired
    @Qualifier("compiledPsychStreamGraph")
    private CompiledGraph compiledPsychStreamGraph;


    @Transactional
    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // ========== Step 1: 创建数据库会话 ==========
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);
//        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);

        // ========== Step 2: 查询情绪日记 ==========
        List<EmotionDiaryWithBLOBs> recentDiaries = emotionDiaryService.getRecent7DaysDiaries(userId);
        String formattedDiaries = formatDiariesForState(recentDiaries);

        // ========== Step 3: 调用 Agent ==========
        // 3.1 组装 inputState（key 必须和 instruction 里 {{xxx}} 对应）
        Map<String, Object> inputState = new HashMap<>();
        inputState.put("diaries", formattedDiaries);          // 对应 {{diaries}}
        inputState.put("initialMessage", createDTO.getInitialMessage() != null
                ? createDTO.getInitialMessage() : "（用户还没说具体内容）");  // 对应 {{initialMessage}}

        // 3.2 调用 Agent
        String emotionSummary = "情绪分析暂时不可用";
        String greetingText = "你好！很高兴见到你，最近怎么样？";

        try {
            Optional<OverAllState> resultState = emotionAnalyzeAgent.invoke(inputState);

            if (resultState.isPresent()) {
                OverAllState state = resultState.get();
                // 3.3 从 State 取 outputKey = "emotionAnalysisResult" 的值
                Optional<Object> agentOutput = state.value("emotionAnalysisResult");
                if (agentOutput.isPresent()) {
                    Object agentOutputValue = agentOutput.get();
                    if (agentOutputValue instanceof AssistantMessage) {
                        AssistantMessage assistantMessage = (AssistantMessage) agentOutputValue;
                        String text = assistantMessage.getText();
                        Map<String, Object> aiResult = parseAiJsonContent(text);
                        emotionSummary = (String) aiResult.getOrDefault("emotionSummary", emotionSummary);
                        greetingText = (String) aiResult.getOrDefault("greetingText", greetingText);
                    }

                }
            }
        } catch (Exception e) {
            // Agent 调用失败，用默认值
            System.err.println("⚠️ emotionAnalyzeAgent 调用失败: " + e.getMessage());
            e.printStackTrace();
        }

        Map<String, Object> fullAnalysis = new HashMap<>();
        fullAnalysis.put("emotionSummary", emotionSummary);
        fullAnalysis.put("greetingText", greetingText);
        fullAnalysis.put("riskLevel", "LOW");
        fullAnalysis.put("updatedAt", LocalDateTime.now().toString());
        String jsonToStore = JSONUtil.toJsonStr(fullAnalysis);

        consultationSessionService.updateSessionEmotion(consultationSession.getId(), jsonToStore);
        // ========== Step 4: 保存 AI 回复到数据库 ==========
        consultationMessageService.saveAimessage(consultationSession.getId(), greetingText, "emotion_analyze");
        // ========== Step 4: 回写 Session ==========
//        consultationSessionService.updateSessionEmotion(consultationSession.getId(), emotionSummary);

        // ========== Step 5: 组装返回值 ==========
        String sessionId = "session_" + consultationSession.getId();
        StructOutPut.EmotionTrend emotionTrend = buildEmotionTrend(recentDiaries);
        List<StructOutPut.EmotionDiarySummary> diarySummaries = buildRecentDiaries(recentDiaries);

        return new StructOutPut.StreamChatSession(
                sessionId, userId, createDTO.getInitialMessage(),
                System.currentTimeMillis(), System.currentTimeMillis() + 86400000L,
                1, "ACTIVE",
                emotionTrend, emotionSummary, greetingText, diarySummaries
        );
    }

    // ========== 下面的辅助方法保持不变 ==========
    private String formatDiariesForState(List<EmotionDiaryWithBLOBs> diaries) {
        if (diaries == null || diaries.isEmpty()) return "（用户暂无情绪历史记录）";
        return diaries.stream()
                .map(d -> """
                        日期: %s
                        心情分数(1-10): %d
                        主导情绪: %s
                        睡眠质量(1-5): %s
                        压力等级(1-5): %s
                        情绪触发原因: %s
                        日记内容: %s
                        AI情感分析: %s
                        """.formatted(d.getDiaryDate(), d.getMoodScore(),
                        orDefault(d.getDominantEmotion(), "未记录"),
                        d.getSleepQuality() != null ? d.getSleepQuality() : "未记录",
                        d.getStressLevel() != null ? d.getStressLevel() : "未记录",
                        orDefault(d.getEmotionTriggers(), "未记录"),
                        orDefault(d.getDiaryContent(), "未记录"),
                        orDefault(d.getAiEmotionAnalysis(), "无")))
                .collect(Collectors.joining("\n---\n"));
    }

    private Map<String, Object> parseAiJsonContent(Object content) {
        try {
            String jsonStr = content.toString().trim();
            if (jsonStr.startsWith("```")) {
                int firstBrace = jsonStr.indexOf('{');
                int lastBrace = jsonStr.lastIndexOf('}');
                if (firstBrace > 0 && lastBrace > firstBrace) {
                    jsonStr = jsonStr.substring(firstBrace, lastBrace + 1);
                }
            }
            JSONObject obj = JSONUtil.parseObj(jsonStr);
            Map<String, Object> result = new HashMap<>();
            result.put("emotionSummary", obj.getStr("emotionSummary", ""));
            result.put("greetingText", obj.getStr("greetingText", ""));
            result.put("riskLevel", obj.getStr("riskLevel", "LOW"));
            return result;
        } catch (Exception e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("emotionSummary", content.toString());
            fallback.put("greetingText", "你好，最近怎么样？");
            fallback.put("riskLevel", "LOW");
            return fallback;
        }
    }

    private StructOutPut.EmotionTrend buildEmotionTrend(List<EmotionDiaryWithBLOBs> diaries) {
        if (diaries == null || diaries.isEmpty()) {
            return new StructOutPut.EmotionTrend(null, null, null, "STABLE", List.of());
        }
        double avgMood = diaries.stream().mapToInt(d -> d.getMoodScore() != null ? d.getMoodScore() : 0).average().orElse(0);
        double avgSleep = diaries.stream().mapToInt(d -> d.getSleepQuality() != null ? d.getSleepQuality() : 0).average().orElse(0);
        double avgStress = diaries.stream().mapToInt(d -> d.getStressLevel() != null ? d.getStressLevel() : 0).average().orElse(0);
        return new StructOutPut.EmotionTrend(
                (int) Math.round(avgMood), (int) Math.round(avgSleep), (int) Math.round(avgStress),
                calculateTrend(diaries),
                diaries.stream().map(d -> new StructOutPut.DailyMood(d.getDiaryDate(), d.getMoodScore(), d.getDominantEmotion())).toList()
        );
    }

    private String calculateTrend(List<EmotionDiaryWithBLOBs> diaries) {
        if (diaries.size() < 2) return "STABLE";
        int mid = diaries.size() / 2;
        double newAvg = diaries.subList(0, mid).stream().mapToInt(d -> d.getMoodScore() != null ? d.getMoodScore() : 0).average().orElse(0);
        double oldAvg = diaries.subList(mid, diaries.size()).stream().mapToInt(d -> d.getMoodScore() != null ? d.getMoodScore() : 0).average().orElse(0);
        if (newAvg - oldAvg > 0.5) return "UP";
        if (oldAvg - newAvg > 0.5) return "DOWN";
        return "STABLE";
    }

    private List<StructOutPut.EmotionDiarySummary> buildRecentDiaries(List<EmotionDiaryWithBLOBs> diaries) {
        if (diaries == null || diaries.isEmpty()) return List.of();
        return diaries.stream().map(d -> new StructOutPut.EmotionDiarySummary(d.getDiaryDate(), d.getMoodScore(), d.getDominantEmotion(), d.getAiEmotionAnalysis())).toList();
    }

    private String orDefault(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }


    public reactor.core.publisher.Flux<com.alibaba.cloud.ai.graph.NodeOutput> streamChat(
            Long sessionId, String userMessage) {

        log.info("StateGraph.streamChat: sessionId={}, userMessage={}", sessionId, userMessage);

        // 1. 保存用户消息
        consultationMessageService.saveUserMessage(sessionId, userMessage, null);

        // 2. 查询情绪分析
        ConsultationSession session = consultationSessionService.getById(sessionId);
        String emotionSummary = "";
        if (session != null && session.getLastEmotionAnalysis() != null) {
            try {
                cn.hutool.json.JSONObject obj = JSONUtil.parseObj(session.getLastEmotionAnalysis());
                emotionSummary = obj.getStr("emotionSummary", "");
            } catch (Exception ignored) {}
        }

        // 3. 查历史对话
        String chatHistory = consultationMessageService.formatHistoryAsText(sessionId);

        // 4. 把所有动态信息拼成一个完整的 userMessage
        String enrichedUserMessage = """
                【用户情绪分析摘要】
                %s

                【历史对话】
                %s

                【用户最新消息】
                %s
                """.formatted(
                emotionSummary.isBlank() ? "暂无" : emotionSummary,
                chatHistory,
                userMessage);

        // 5. StateGraph 输入 —— 只需要一个 key！
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("userMessage", enrichedUserMessage);

        log.info("StateGraph inputs: enrichedUserMessage长度={}", enrichedUserMessage.length());

        // 6. 启动
        return compiledPsychStreamGraph.stream(inputs)
                .doOnError(e -> log.error("StateGraph 执行出错", e))
                .doOnComplete(() -> log.info("StateGraph 完成, sessionId={}", sessionId));
    }
}