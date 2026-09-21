package com.atguigu.study.mapper;

import com.atguigu.study.domain.SysFileInfo;
import com.atguigu.study.domain.SysFileInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysFileInfoMapper {
    long countByExample(SysFileInfoExample example);

    int deleteByExample(SysFileInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SysFileInfo record);

    int insertSelective(SysFileInfo record);

    List<SysFileInfo> selectByExample(SysFileInfoExample example);

    SysFileInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SysFileInfo record, @Param("example") SysFileInfoExample example);

    int updateByExample(@Param("record") SysFileInfo record, @Param("example") SysFileInfoExample example);

    int updateByPrimaryKeySelective(SysFileInfo record);

    int updateByPrimaryKey(SysFileInfo record);
}