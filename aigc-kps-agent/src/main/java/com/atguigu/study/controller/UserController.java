package com.atguigu.study.controller;

import com.atguigu.study.common.Result;
import com.atguigu.study.dto.command.UserLoginCommandDTO;
import com.atguigu.study.dto.command.UserRegisterCommandDTO;
import com.atguigu.study.dto.response.UserLoginResponseDTO;
import com.atguigu.study.service.UserService;
import com.atguigu.study.util.JwtTokenUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    // 用户登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO) {
        UserLoginResponseDTO result = userService.login(commandDTO);
        return Result.ok(result);
    }

    // 用户注册接口
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO commandDTO) {
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.register(commandDTO);
        return Result.ok(result);
    }

    // 获取当前用户
    @GetMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> getCurrentUser() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.getUserById(userId);
        return Result.ok(result);
    }

    // 用户注销登录接口
    @PostMapping("/logout")
    public Result<String> logout() {
        String token = JwtTokenUtil.getCurrentToken();
        if (token != null) {
            JwtTokenUtil.addToBlacklist(token);
        }
        return Result.ok("注销成功");
    }

}