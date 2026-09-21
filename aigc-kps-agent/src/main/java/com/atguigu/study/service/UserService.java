package com.atguigu.study.service;

import cn.hutool.json.JSONUtil;
import com.atguigu.study.domain.User;
import com.atguigu.study.domain.UserExample;
import com.atguigu.study.dto.command.UserLoginCommandDTO;
import com.atguigu.study.dto.command.UserRegisterCommandDTO;
import com.atguigu.study.dto.response.UserLoginResponseDTO;
import com.atguigu.study.enumClass.UserType;
import com.atguigu.study.exception.BusinessException;
import com.atguigu.study.mapper.UserMapper;
import com.atguigu.study.service.convert.UserConvert;
import com.atguigu.study.util.JwtTokenUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO) {
        // 构建查询条件
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(commandDTO.getUsername());
        // 调用MP API查询
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() != 1) {
            throw new BusinessException("用户不存在");
        }

        User user = users.get(0);
        System.out.println(user);

        // 判断用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 验证密码
        String inputPassword = commandDTO.getPassword().trim();
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 检查用户的状态
        if (!user.getStatus()) {
            throw new BusinessException("用户已被禁用，请联系管理员");
        }

        // 生成JWT token
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        System.out.println(token);
        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
        return UserConvert.entityToLoginResponse(token, userInfo);
    }

    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO commandDTO) {
        System.out.println(JSONUtil.parseObj(commandDTO));
        // 验证密码是否一致
        if (!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入密码不一致");
        }

        // 检查用户名是否存在


        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(commandDTO.getUsername());
        List<User> users = userMapper.selectByExample(userExample);
        if (users != null && users.size() > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否存在
        UserExample emailQuery = new UserExample();
        emailQuery.createCriteria().andEmailEqualTo(commandDTO.getEmail());
        List<User> emailUsers = userMapper.selectByExample(emailQuery);
        if (emailUsers != null && emailUsers.size() > 0) {
            throw new BusinessException("邮箱已存在");
        }

        // 用户类型
        if (!UserType.isValidCode(commandDTO.getUserType())) {
            throw new BusinessException("无效的用户类型");
        }

        // 创建用户
        String password = commandDTO.getPassword().trim();
        String encodedPassword = passwordEncoder.encode(password);
        User user = UserConvert.registerCommandToEntity(commandDTO, encodedPassword);

        // 插入数据库
        userMapper.insert(user);

        return UserConvert.entityToDetailResponse(user);
    }

    public UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }

}
