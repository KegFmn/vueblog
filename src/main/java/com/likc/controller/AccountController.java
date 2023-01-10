package com.likc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.dto.LoginDto;
import com.likc.common.lang.Result;
import com.likc.entity.User;
import com.likc.mapstruct.MapStructConverter;
import com.likc.service.UserService;
import com.likc.util.JwtUtils;
import com.likc.vo.UserVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MapStructConverter mapStructConverter;

    @PostMapping("/login")
    public Result<UserVo> login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){

        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", loginDto.getUserName()));
        Assert.notNull(user,"用户不存在");

        if (!user.getPassWord().equals(DigestUtils.md5Hex(loginDto.getPassWord()))){
            return new Result<>(400, "密码不正确");
        }
        HashMap<String, String> payload = new HashMap<>(16);
        payload.put("id", user.getId().toString());
        String jwt = jwtUtils.createJwt(payload);
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        UserVo userVo = mapStructConverter.userEntity2Vo(user);

        return new Result<>(200, "登录成功", userVo);
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result<Void> logout(){

        SecurityUtils.getSubject().logout();
        return new Result<>(200, "退出成功");
    }

}
