package com.atguigu.study.service.ai;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.domain.ConsultationSessionExample;
import com.atguigu.study.domain.User;
import com.atguigu.study.dto.command.ConsultationSessionCreateDTO;
import com.atguigu.study.mapper.ConsultationSessionMapper;
import com.atguigu.study.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationSessionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // 验证用户是否存在
        User user =userMapper.selectByPrimaryKey(userId);
        if (user != null) {
            // 创建会话记录
             ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            // 如果未提供标题
            if (StrUtil.isBlank(createDTO.getSessionTitle())) {
                session.setSessionTitle(String.format("知心AI助手 - " + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm")));
            }

            // 插入记录
            consultationSessionMapper.insert(session);
            return session;
        }

        return null;
    }

    /**
     * 更新会话的 AI 情绪分析结果
     *
     * @param sessionId         会话 ID
     * @param emotionAnalysis   情绪分析结果（JSON 字符串）
     */
    public void updateSessionEmotion(Long sessionId, String emotionAnalysis) {
        ConsultationSessionExample example = new ConsultationSessionExample();
        example.createCriteria().andIdEqualTo(sessionId);

        ConsultationSession update = new ConsultationSession();
        update.setLastEmotionAnalysis(emotionAnalysis);
        update.setLastEmotionUpdatedAt(LocalDateTime.now());

        consultationSessionMapper.updateByExampleSelective(update, example);
    }

    // ConsultationSessionService.java 末尾加这个方法

    /**
     * 根据 sessionId 查询会话
     */
    public ConsultationSession getById(Long sessionId) {
        ConsultationSessionExample example = new ConsultationSessionExample();
        example.createCriteria().andIdEqualTo(sessionId);
        return consultationSessionMapper.selectByExample(example).get(0);
    }

    /**
     * 分页查询当前用户的会话列表
     */
    public java.util.Map<String, Object> pageByUserId(Long userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        long total = consultationSessionMapper.countByUserId(userId);
        int offset = (pageNum - 1) * pageSize;
        List<ConsultationSession> records = consultationSessionMapper.selectByUserIdPage(userId, offset, pageSize);

        java.util.Map<String, Object> page = new java.util.HashMap<>();
        page.put("records", records);
        page.put("total", total);
        page.put("pageNum", pageNum);
        page.put("pageSize", pageSize);
        return page;
    }

    /**
     * 删除会话
     */
    public void deleteById(Long sessionId, Long userId) {
        ConsultationSessionExample example = new ConsultationSessionExample();
        example.createCriteria().andIdEqualTo(sessionId).andUserIdEqualTo(userId);
        consultationSessionMapper.deleteByExample(example);
    }

}
