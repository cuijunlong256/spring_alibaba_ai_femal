package com.atguigu.study.mapper;

import com.atguigu.study.domain.UserFavorite;
import com.atguigu.study.domain.UserFavoriteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserFavoriteMapper {
    long countByExample(UserFavoriteExample example);

    int deleteByExample(UserFavoriteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UserFavorite record);

    int insertSelective(UserFavorite record);

    List<UserFavorite> selectByExample(UserFavoriteExample example);

    UserFavorite selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UserFavorite record, @Param("example") UserFavoriteExample example);

    int updateByExample(@Param("record") UserFavorite record, @Param("example") UserFavoriteExample example);

    int updateByPrimaryKeySelective(UserFavorite record);

    int updateByPrimaryKey(UserFavorite record);
}