package com.atguigu.study.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.atguigu.study.common.Result;
import com.atguigu.study.domain.ConsultationMessage;
import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.dto.command.ConsultationSessionCreateDTO;
import com.atguigu.study.dto.command.ConsultationStreamDTO;
import com.atguigu.study.service.StructOutPut;
import com.atguigu.study.service.ai.ConsultationMessageService;
import com.atguigu.study.service.ai.ConsultationSessionService;
import com.atguigu.study.service.ai.PsychologicalSupportService;
import com.atguigu.study.util.JwtTokenUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import com.alibaba.cloud.ai.graph.streaming.OutputType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChat {

    @Autowired
    private PsychologicalSupportService psychologicalSupportService;

    @Autowired
    private ConsultationMessageService consultationMessageService;

    @Autowired
    private ConsultationSessionService consultationSessionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(@Valid @RequestBody ConsultationSessionCreateDTO createDTO) throws GraphRunnerException {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        StructOutPut.StreamChatSession session = psychologicalSupportService.startSession(userId, createDTO);
        return Result.ok(session);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Valid @RequestBody ConsultationStreamDTO streamDTO) {
        log.info("========== /stream 接口触发 ==========");
        log.info("sessionId={}, userMessage={}", streamDTO.getSessionId(), streamDTO.getUserMessage());

        Long dbSessionId = parseSessionId(streamDTO.getSessionId());
        String userMessage = streamDTO.getUserMessage();
        AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());
        AtomicReference<Boolean> isEmergency = new AtomicReference<>(false);

        return psychologicalSupportService.streamChat(dbSessionId, userMessage)
                .flatMap(nodeOutput -> {
                    // 统一走 convertToSse，按节点严格过滤
                    ServerSentEvent<String> event = convertToSse(nodeOutput, fullResponse, isEmergency);
                    if (event == null || event.data() == null) {
                        return Flux.empty();
                    }
                    return Flux.just(event);
                })
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data(buildOkData(""))
                                .build()
                ))
                .onErrorContinue((e, item) -> {
                    if (e instanceof java.io.IOException || e.getCause() instanceof java.io.IOException) {
                        // 客户端断开，静默处理
                    } else {
                        log.warn("SSE 事件发送异常，跳过: {}", e.getMessage());
                    }
                })
                .doFinally(signalType -> {
                    String fullText = fullResponse.get().toString();
                    if (!fullText.isEmpty()) {
                        consultationMessageService.saveAimessage(dbSessionId, fullText,
                                isEmergency.get() ? "human_review" : "psychological_guide");
                    }
                    log.info("========== /stream 结束({}) ========== 回复长度={}, 高危={}",
                            signalType, fullText.length(), isEmergency.get());
                })
                .onErrorResume(e -> {
                    if (e instanceof java.io.IOException || e.getCause() instanceof java.io.IOException) {
                        return Flux.empty();
                    }
                    log.error("streamChat 出错", e);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data(buildErrorData(e.getMessage()))
                            .build());
                });
    }

    @GetMapping("/sessions")
    public Result<Map<String, Object>> getSessionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        Map<String, Object> page = consultationSessionService.pageByUserId(userId, pageNum, pageSize);
        return Result.ok(page);
    }

    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        consultationSessionService.deleteById(id, userId);
        return Result.ok();
    }

    @GetMapping("/sessions/{id}/messages")
    public Result<List<ConsultationMessage>> getSessionMessages(@PathVariable String id) {
        Long dbId = parseSessionId(id);
        List<ConsultationMessage> msgs = consultationMessageService.listBySessionId(dbId);
        return Result.ok(msgs);
    }

    @GetMapping("/session/{id}/emotion")
    public Result<String> getSessionEmotion(@PathVariable String id) {
        Long dbId = parseSessionId(id);
        ConsultationSession session = consultationSessionService.getById(dbId);
        if (session != null && session.getLastEmotionAnalysis() != null) {
            return Result.ok(session.getLastEmotionAnalysis());
        }
        return Result.ok("");
    }

    private Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }

    private String buildErrorData(String message) {
        try {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("code", "500");
            wrapper.put("message", message);
            return objectMapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return "{\"code\":\"500\",\"message\":\"unknown error\"}";
        }
    }

    private String buildOkData(String content) {
        try {
            Map<String, Object> inner = new HashMap<>();
            inner.put("content", content);
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("code", "200");
            wrapper.put("data", inner);
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            return "{\"code\":\"200\",\"data\":{\"content\":\"\"}}";
        }
    }

    /**
     * 核心改动：按当前节点严格过滤输出，防止 state 残留数据被误推
     */
    private ServerSentEvent<String> convertToSse(NodeOutput nodeOutput,
                                                 AtomicReference<StringBuilder> fullResponse,
                                                 AtomicReference<Boolean> isEmergency) {
        if (nodeOutput == null) return null;
        if (nodeOutput.isSTART() || nodeOutput.isEND()) return null;

        String currentNode = nodeOutput.node();
        String agentName = nodeOutput.agent();
        OverAllState state = nodeOutput.state();

        log.debug("当前节点: node={}, agent={}", currentNode, agentName);

        // ====== 1. 风险评估节点：完全跳过（不输出任何东西） ======
        if ("riskAssessmentAgent".equals(currentNode)
                || (agentName != null && agentName.contains("riskAssessmentAgent"))) {
            return null;
        }

        // ====== 2. 人工审核节点：只推送 finalResponse ======
        if ("human_review".equals(currentNode)) {
            if (state != null && fullResponse.get().isEmpty()) {
                Object finalResp = state.value("finalResponse").orElse(null);
                if (finalResp != null && !finalResp.toString().isBlank()) {
                    String text = finalResp.toString();
                    fullResponse.get().append(text);
                    isEmergency.set(true);
                    return ServerSentEvent.<String>builder()
                            .event("agent_human_review")
                            .data(buildOkData(text))
                            .build();
                }
            }
            return null;
        }

        // ====== 3. 心理引导节点：只处理 guideResponse 相关输出 ======
        if ("psychologicalGuideAgent".equals(currentNode)
                || (agentName != null && agentName.contains("psychologicalGuideAgent"))) {

            if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                OutputType outputType = streamingOutput.getOutputType();

                if (outputType != null && outputType.name().contains("HOOK")) {
                    return null;
                }

                // 流式 token
                if (outputType == OutputType.AGENT_MODEL_STREAMING) {
                    Message msg = streamingOutput.message();
                    if (msg != null) {
                        String text = msg.getText();
                        if (text != null && !text.isEmpty()) {
                            text = filterJson(text);
                            if (text.isEmpty()) return null;
                            fullResponse.get().append(text);
                            return ServerSentEvent.<String>builder()
                                    .data(buildOkData(text))
                                    .build();
                        }
                    }
                }

                // 完成输出兜底
                if (outputType == OutputType.AGENT_MODEL_FINISHED) {
                    Message msg = streamingOutput.message();
                    if (msg != null && !fullResponse.get().isEmpty()) {
                        return null;
                    }
                    if (msg != null) {
                        String text = msg.getText();
                        if (text != null && !text.isEmpty()) {
                            text = filterJson(text);
                            if (text.isEmpty()) return null;
                            fullResponse.get().append(text);
                            return ServerSentEvent.<String>builder()
                                    .data(buildOkData(text))
                                    .build();
                        }
                    }
                }
                return null;
            }

            // 非流式兜底
            if (state != null && fullResponse.get().isEmpty()) {
                Object guideResp = state.value("guideResponse").orElse(null);
                if (guideResp instanceof AssistantMessage am) {
                    String text = am.getText();
                    if (text != null && !text.isEmpty()) {
                        text = filterJson(text);
                        if (text.isEmpty()) return null;
                        fullResponse.get().append(text);
                        return ServerSentEvent.<String>builder()
                                .data(buildOkData(text))
                                .build();
                    }
                }
            }
            return null;
        }

        // 其他节点一律跳过
        return null;
    }

    /**
     * 过滤掉回复中的 JSON 内容
     */
    private String filterJson(String text) {
        if (text == null || text.isEmpty()) return text;
        int jsonStart = text.indexOf('{');
        if (jsonStart > 0) {
            return text.substring(0, jsonStart).trim();
        }
        if (jsonStart == 0) {
            return "";
        }
        return text;
    }

    private Long parseSessionId(String sessionId) {
        if (sessionId != null && sessionId.startsWith("session_")) {
            return Long.parseLong(sessionId.substring("session_".length()));
        }
        return Long.parseLong(sessionId);
    }
}