package com.atguigu.study.service.ai;

import com.atguigu.study.domain.ConsultationMessage;
import com.atguigu.study.domain.ConsultationMessageExample;
import com.atguigu.study.mapper.ConsultationMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 红娘消息服务（独立于心理咨询）
 * 共用 consultation_message 表
 */
@Slf4j
@Service
public class MatchmakerMessageService {

    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;

    /**
     * 保存用户消息
     */
    public ConsultationMessage saveUserMessage(Long sessionId, String content) {
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(message);
        return message;
    }

    /**
     * 保存AI回复消息
     */
    public ConsultationMessage saveAimessage(Long sessionId, String content, String aiModel) {
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)
                .messageType(1)
                .content(content)
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();
        consultationMessageMapper.insert(message);
        return message;
    }

    /**
     * 查询会话的所有消息（按时间升序）
     */
    public List<ConsultationMessage> listBySessionId(Long sessionId) {
        ConsultationMessageExample example = new ConsultationMessageExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);
        example.setOrderByClause("created_at ASC");
        return consultationMessageMapper.selectByExample(example);
    }

    /**
     * 把历史消息拼接成纯文本，用于 Agent 上下文
     */
    public String formatHistoryAsText(Long sessionId) {
        List<ConsultationMessage> msgs = listBySessionId(sessionId);
        if (msgs == null || msgs.isEmpty()) return "（暂无历史对话）";
        StringBuilder sb = new StringBuilder();
        for (ConsultationMessage m : msgs) {
            if (m.getContent() == null) continue;
            if (m.getSenderType() != null && m.getSenderType() == 1) {
                sb.append("用户：").append(m.getContent()).append("\n");
            } else if (m.getSenderType() != null && m.getSenderType() == 2) {
                sb.append("红娘：").append(m.getContent()).append("\n");
            }
        }
        return sb.toString();
    }
}