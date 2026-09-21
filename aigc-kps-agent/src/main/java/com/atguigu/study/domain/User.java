package com.atguigu.study.domain;

import com.atguigu.study.enumClass.UserStatus;
import com.atguigu.study.enumClass.UserType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class User {
    private Long id;

    private String username;

    private String email;

    private String phone;

    private String password;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private Integer userType;

    private Boolean status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserTypeDisplayName() {
        try {
            return UserType.fromCode(userType).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }
    /**
     * 获取用户状态显示名称
     */
    public String getStatusDisplayName() {
        try {
            return UserStatus.fromCode(status).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }

    public String getDisplayName() {
        return nickname != null && !nickname.trim().isEmpty() ? nickname : username;
    }
}