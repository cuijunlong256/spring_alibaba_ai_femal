package com.atguigu.study.mapper;

import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.domain.ConsultationSessionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ConsultationSessionMapper {
    long countByExample(ConsultationSessionExample example);

    int deleteByExample(ConsultationSessionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ConsultationSession record);

    int insertSelective(ConsultationSession record);

    List<ConsultationSession> selectByExample(ConsultationSessionExample example);

    ConsultationSession selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ConsultationSession record, @Param("example") ConsultationSessionExample example);

    int updateByExample(@Param("record") ConsultationSession record, @Param("example") ConsultationSessionExample example);

    int updateByPrimaryKeySelective(ConsultationSession record);

    int updateByPrimaryKey(ConsultationSession record);

    List<ConsultationSession> selectByUserIdPage(@Param("userId") Long userId,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);

    /**
     * 根据用户ID统计会话总数
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和业务类型分页查询会话
     */
    List<ConsultationSession> selectByUserIdAndTypePage(@Param("userId") Long userId,
                                                        @Param("type") String type,
                                                        @Param("offset") int offset,
                                                        @Param("pageSize") int pageSize);
}
