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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 红娘会话服务（独立于心理咨询）
 * 共用 consultation_session 表，通过 type 字段区分业务
 */
@Service
public class MatchmakerSessionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    /**
     * 创建红娘会话
     * @param type FEMALE_MATCHMAKER(推荐女性) / MALE_MATCHMAKER(推荐男性)
     */
    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO, String type) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) return null;

        ConsultationSession session = ConsultationSession.builder()
                .userId(userId)
                .sessionTitle(createDTO.getSessionTitle())
                .startedAt(LocalDateTime.now())
                .type(type)
                .build();

        if (StrUtil.isBlank(createDTO.getSessionTitle())) {
            session.setSessionTitle(String.format("情感红娘 - " + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm")));
        }

        consultationSessionMapper.insert(session);
        return session;
    }

    /**
     * 根据ID查询会话
     */
    public ConsultationSession getById(Long sessionId) {
        ConsultationSessionExample example = new ConsultationSessionExample();
        example.createCriteria().andIdEqualTo(sessionId);
        List<ConsultationSession> list = consultationSessionMapper.selectByExample(example);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * 分页查询用户的红娘会话列表
     */
    public Map<String, Object> pageByUserId(Long userId, Integer pageNum, Integer pageSize, String type) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;
        List<ConsultationSession> records = consultationSessionMapper.selectByUserIdAndTypePage(userId, type, offset, pageSize);

        // 统计总数
        ConsultationSessionExample countExample = new ConsultationSessionExample();
        countExample.createCriteria().andUserIdEqualTo(userId).andTypeEqualTo(type);
        long total = consultationSessionMapper.countByExample(countExample);

        Map<String, Object> page = new HashMap<>();
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