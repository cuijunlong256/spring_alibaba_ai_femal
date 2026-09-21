package com.atguigu.study.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "partner", autoResultMap = true)
public class Partner {

    private String id;
    private String name;
    private String nameEn;
    private String category;
    private String gender;
    private String nationality;
    private Integer birthYear;
    private Integer age;
    private String zodiac;
    private String bloodType;
    private Integer heightCm;
    private Integer weightKg;

    private String idealPartner;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> appearance;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] personality;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] interests;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] tags;
}