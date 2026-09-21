package com.atguigu.study.service;

import com.atguigu.study.domain.EmotionDiaryExample;
import com.atguigu.study.domain.EmotionDiaryWithBLOBs;
import com.atguigu.study.mapper.EmotionDiaryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class EmotionDiaryService {

    @Autowired
    private EmotionDiaryMapper emotionDiaryMapper;


    /**
     * 查询用户最近 N 天的情绪日记
     *
     * @param userId 用户ID
     * @param days 天数
     * @return 情绪日记列表
     */
    public List<EmotionDiaryWithBLOBs> getRecentDiaries(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        EmotionDiaryExample example = new EmotionDiaryExample();
        example.createCriteria().andUserIdEqualTo(userId).andDiaryDateGreaterThanOrEqualTo(startDate);
        example.setOrderByClause("diary_date desc");

        return emotionDiaryMapper.selectByExampleWithBLOBs(example);

    }

    /**
     * 查询用户最近 7 天的情绪日记
     */
    public List<EmotionDiaryWithBLOBs> getRecent7DaysDiaries(Long userId) {
        return getRecentDiaries(userId, 7);
    }


    /**
     * 查询用户最近 30 天的情绪日记
     */
    public List<EmotionDiaryWithBLOBs> getRecent30DaysDiaries(Long userId) {
        return getRecentDiaries(userId, 30);
    }
}
