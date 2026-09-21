package com.atguigu.study.service.ai;

import com.atguigu.study.domain.ConsultationMessage;
import com.atguigu.study.domain.ConsultationMessageExample;
import com.atguigu.study.dto.response.ConsultationMessageResponseDTO;
import com.atguigu.study.mapper.ConsultationMessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ConsultationMessageService {
    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;

/**
 * 保存用户消息的方法
 * @param sessionId 会话ID
 * @param content 消息内容
 * @param emotion_tag 情感标签
 * @return 返回保存后的用户消息实体
 */
    public ConsultationMessage saveUserMessage(Long sessionId, String content, String emotion_tag) {
        // 构建用户消息实体
         ConsultationMessage userMessage = ConsultationMessage.builder()
            .sessionId(sessionId)  // 设置会话ID
            .senderType(1)        // 发送者类型，1表示用户
            .messageType(1)       // 消息类型，1表示文本消息
            .content(content)     // 消息内容
            .emotionTag(emotion_tag)  // 情感标签
            .createdAt(LocalDateTime.now())  // 创建时间，设置为当前时间
            .build();

        consultationMessageMapper.insert(userMessage);  // 将消息插入数据库
        return userMessage;  // 返回保存后的消息实体
    }

/**
 * 保存AI回复的消息
 * @param sessionId 会话ID
 * @param content 消息内容
 * @param aiModel AI模型名称
 * @return ConsultationMessage 保存后的消息对象
 */
    public ConsultationMessage saveAimessage(Long sessionId, String content, String aiModel) {
    // 使用建造者模式创建ConsultationMessage对象
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)          // 设置会话ID
                .senderType(2)                // 设置发送者类型为AI
                .messageType(1)               // 设置消息类型为文本消息
                .content(content)             // 设置消息内容
                .aiModel(aiModel)             // 设置使用的AI模型
                .createdAt(LocalDateTime.now()) // 设置创建时间为当前时间
                .build();
        // 插入数据库
        consultationMessageMapper.insert(message);
        return message;
    }

    public Integer getMessageCountBySessionId(Long sessionId) {
//        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId);
        //        queryWrapper.eq(ConsultationMessage::getSenderType, 1);
        ConsultationMessageExample example = new ConsultationMessageExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);

       Long count = consultationMessageMapper.selectByExample(example).stream().count();
       log.info("Message count for session {} is {}", sessionId, count);
       return count.intValue();
    }

    // 获取会话的最后一条消息
    public ConsultationMessageResponseDTO getLastMessageBySessionId(Long sessionId) {
//        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId)
//                .orderByDesc(ConsultationMessage::getCreatedAt)
//                .last("limit 1");
        ConsultationMessageExample example = new ConsultationMessageExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);

        List<ConsultationMessage> lastMessageList = consultationMessageMapper.selectByExample(example);
        return lastMessageList != null && !lastMessageList.isEmpty() ? convertToResponseDTO(lastMessageList.get(0)) : null;

    }

    private ConsultationMessageResponseDTO convertToResponseDTO(ConsultationMessage message) {
        if (message == null) {
            return null;
        }

        // 手动逐字段赋值，确保转换的准确性和可控性
        ConsultationMessageResponseDTO responseDTO = new ConsultationMessageResponseDTO();
        responseDTO.setId(message.getId());
        responseDTO.setSessionId(message.getSessionId());
        responseDTO.setSenderType(message.getSenderType());
        responseDTO.setMessageType(message.getMessageType());
        responseDTO.setContent(message.getContent());
        responseDTO.setEmotionTag(message.getEmotionTag());
        responseDTO.setAiModel(message.getAiModel());
        responseDTO.setCreatedAt(message.getCreatedAt());

        // 设置描述字段（通过实体方法获取）
        responseDTO.setSenderTypeDesc(message.getSenderType().toString());
        responseDTO.setMessageTypeDesc(message.getMessageType().toString());

        // 计算消息长度
        responseDTO.calculateContentLength();

        return responseDTO;
    }

    // ConsultationMessageService.java 末尾加这两个方法

    /**
     * 查询某个会话的所有消息（按创建时间升序）
     */
    public List<ConsultationMessage> listBySessionId(Long sessionId) {
        ConsultationMessageExample example = new ConsultationMessageExample();
        example.createCriteria().andSessionIdEqualTo(sessionId);
        example.setOrderByClause("created_at ASC");
        return consultationMessageMapper.selectByExample(example);
    }

    /**
     * 把数据库历史消息转成 Spring AI 的 Message 列表
     * senderType=1 → UserMessage
     * senderType=2 → AssistantMessage
     */
    public List<org.springframework.ai.chat.messages.Message> loadHistoryAsMessages(Long sessionId) {
        List<ConsultationMessage> dbList = listBySessionId(sessionId);
        List<Message> result = new ArrayList<>();
        for (ConsultationMessage message : dbList) {
            if (message.getContent() ==null || message.getContent().isEmpty()) {
                continue;
            }
            if (message.getSenderType() != null && message.getSenderType() == 1) {
                // 用户消息
                result.add(new org.springframework.ai.chat.messages.UserMessage(message.getContent()));
            } else if (message.getSenderType() != null && message.getSenderType() == 2) {
                // AI 消息
                result.add(new org.springframework.ai.chat.messages.AssistantMessage(message.getContent()));
            }
        }
        log.info("loadHistoryAsMessages: sessionId={}, 共 {} 条历史消息", sessionId, result.size());
        return result;
    }

    /**
     * 把历史消息拼接成纯文本格式
     * 用于 Agent 的 Prompt 模板渲染
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
                sb.append("AI：").append(m.getContent()).append("\n");
            }
        }
        return sb.toString();
    }
}
