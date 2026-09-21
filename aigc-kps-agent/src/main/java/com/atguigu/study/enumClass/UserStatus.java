package com.atguigu.study.enumClass;

import lombok.Getter;
@Getter
public enum UserStatus {

    DISABLED(false, "禁用"),
    NORMAL(true, "正常");

    private final Boolean code;
    private final String description;

    UserStatus(Boolean code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static UserStatus fromCode(Boolean code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的用户状态代码: " + code);
    }
}