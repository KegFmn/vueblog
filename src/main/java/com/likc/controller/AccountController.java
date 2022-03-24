package com.likc.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.common.dto.LoginDto;
import com.likc.common.lang.Result;
import com.likc.entity.User;
import com.likc.service.UserService;
import com.likc.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @CrossOrigin
    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){

        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", loginDto.getUserName()));
        Assert.notNull(user,"用户不存在");

        if (!user.getPassWord().equals(SecureUtil.md5(loginDto.getPassWord()))){
            return Result.fail("密码不正确");
        }

        String jwt = jwtUtils.generateToken(user.getUid());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return Result.succ(MapUtil.builder()
                .put("uid",user.getUid())
                .put("userName",user.getUserName())
                .put("avatar",user.getAvatar())
                .put("email",user.getEmail())
                .map()
        );
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout(){

        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

}
