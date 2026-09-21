package com.atguigu.study.pgmapper;

import com.atguigu.study.domain.Partner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PartnerMapper extends BaseMapper<Partner> {

    /**
     * 根据性别查询所有明星
     */
    @Select("SELECT * FROM partner WHERE gender = #{gender}")
    List<Partner> selectByGender(@Param("gender") String gender);

    /**
     * 根据ID列表批量查询
     */
    @Select({
            "<script>",
            "SELECT * FROM partner WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<Partner> selectByIds(@Param("ids") List<String> ids);
}