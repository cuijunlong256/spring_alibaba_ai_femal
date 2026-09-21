package com.atguigu.study.controller;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.streaming.OutputType;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import com.atguigu.study.common.Result;
import com.atguigu.study.domain.ConsultationMessage;
import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.dto.command.ConsultationSessionCreateDTO;
import com.atguigu.study.dto.command.ConsultationStreamDTO;
import com.atguigu.study.service.ai.MatchmakerMessageService;
import com.atguigu.study.service.ai.MatchmakerService;
import com.atguigu.study.service.ai.MatchmakerSessionService;
import com.atguigu.study.util.JwtTokenUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 男性红娘交互接口（针对女性用户，推荐男性）
 */
@Slf4j
@RestController
@RequestMapping("/api/male-matchmaker")
public class MaleGraphChatController {

    /** 业务类型：推荐男性 */
    private static final String TYPE = "MALE_MATCHMAKER";

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private MatchmakerMessageService matchmakerMessageService;

    @Autowired
    private MatchmakerSessionService matchmakerSessionService;

    /**
     * 1. 创建会话
     */
    @PostMapping("/session/start")
    public Result<Map<String, Object>> startSession(@RequestBody ConsultationSessionCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        ConsultationSession session = matchmakerService.startSession(userId, createDTO, TYPE);
        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", "session_" + session.getId());
        data.put("sessionTitle", session.getSessionTitle());
        data.put("startedAt", session.getStartedAt());
        return Result.ok(data);
    }

    /**
     * 2. 流式对话（SSE）
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ConsultationStreamDTO streamDTO) {
        log.info("========== /api/male-matchmaker/stream 触发 ==========");

        Long userId = getCurrentUserId();
        Long dbSessionId = parseSessionId(streamDTO.getSessionId());
        String userMessage = streamDTO.getUserMessage();
        AtomicReference<StringBuilder> fullResponse = new AtomicReference<>(new StringBuilder());
        AtomicReference<Boolean> isEmergency = new AtomicReference<>(false);

        return matchmakerService.streamChat(dbSessionId, userId, userMessage, TYPE)
                .flatMap(nodeOutput -> {
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
                .doFinally(
                        signalType -> {
                            String fullText = fullResponse.get().toString();
                            if (!fullText.isEmpty()) {
                                matchmakerMessageService.saveAimessage(dbSessionId, fullText,
                                        isEmergency.get() ? "human_review" : TYPE.toLowerCase());
                            }
                            log.info("========== male-matchmaker stream 结束({}) 回复长度={} ==========",
                                    signalType, fullText.length());
                        }
                )
                .onErrorResume(e -> {
                    log.error("male-matchmaker streamChat 出错", e);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data(buildErrorData(e.getMessage()))
                            .build());
                });
    }

    /**
     * 3. 会话列表
     */
    @GetMapping("/sessions")
    public Result<Map<String, Object>> getSessionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        Map<String, Object> page = matchmakerSessionService.pageByUserId(userId, pageNum, pageSize, TYPE);
        return Result.ok(page);
    }

    /**
     * 4. 删除会话
     */
    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        matchmakerSessionService.deleteById(id, userId);
        return Result.ok();
    }

    /**
     * 5. 历史消息
     */
    @GetMapping("/sessions/{id}/messages")
    public Result<List<ConsultationMessage>> getSessionMessages(@PathVariable String id) {
        Long dbId = parseSessionId(id);
        List<ConsultationMessage> msgs = matchmakerMessageService.listBySessionId(dbId);
        return Result.ok(msgs);
    }

    // ============ 辅助方法 ============

    private Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }

    private Long parseSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (sessionId.startsWith("session_")) {
            sessionId = sessionId.substring("session_".length());
            while (sessionId.startsWith("session_")) {
                sessionId = sessionId.substring("session_".length());
            }
        }
        return Long.parseLong(sessionId);
    }

    private String buildOkData(String content) {
        try {
            Map<String, Object> inner = new HashMap<>();
            inner.put("content", content);
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("code", "200");
            wrapper.put("data", inner);
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(wrapper);
        } catch (Exception e) {
            return "{\"code\":\"200\",\"data\":{\"content\":\"\"}}";
        }
    }

    private String buildErrorData(String message) {
        try {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("code", "500");
            wrapper.put("message", message);
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(wrapper);
        } catch (Exception e) {
            return "{\"code\":\"500\",\"message\":\"unknown error\"}";
        }
    }

    private ServerSentEvent<String> convertToSse(NodeOutput nodeOutput,
                                                 AtomicReference<StringBuilder> fullResponse,
                                                 AtomicReference<Boolean> isEmergency) {
        if (nodeOutput == null) return null;
        if (nodeOutput.isSTART() || nodeOutput.isEND()) return null;

        String currentNode = nodeOutput.node();
        String agentName = nodeOutput.agent();
        OverAllState state = nodeOutput.state();

        // 1. 风险评估节点：跳过
        if ("maleMatchmakerRiskAgent".equals(currentNode)
                || (agentName != null && agentName.contains("maleMatchmakerRiskAgent"))) {
            return null;
        }

        // 2. 人工审核节点：输出警告
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

        // 3. 红娘推荐节点：流式输出
        if ("maleMatchmakerAgent".equals(currentNode)
                || (agentName != null && agentName.contains("maleMatchmakerAgent"))) {

            if (nodeOutput instanceof StreamingOutput<?> streamingOutput) {
                OutputType outputType = streamingOutput.getOutputType();

                if (outputType != null && outputType.name().contains("HOOK")) {
                    return null;
                }

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
                Object guideResp = state.value("maleMatchmakerAgentResponse").orElse(null);
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

        return null;
    }

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
}