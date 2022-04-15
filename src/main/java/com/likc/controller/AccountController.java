package com.likc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.likc.dto.LoginDto;
import com.likc.common.lang.Result;
import com.likc.entity.User;
import com.likc.service.UserService;
import com.likc.util.JwtUtils;
import com.likc.vo.LoginVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
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

    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){

        User user = userService.getOne(new QueryWrapper<User>().eq("user_name", loginDto.getUserName()));
        Assert.notNull(user,"用户不存在");

        if (!user.getPassWord().equals(DigestUtils.md5Hex(loginDto.getPassWord()))){
            return new Result<>(400, "密码不正确");
        }

        String jwt = jwtUtils.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        LoginVo loginVo = new LoginVo();
        BeanUtils.copyProperties(user, loginVo);

        return new Result<>(200, "登录成功", loginVo);
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result<Void> logout(){

        SecurityUtils.getSubject().logout();
        return new Result<>(200, "退出成功");
    }

}
