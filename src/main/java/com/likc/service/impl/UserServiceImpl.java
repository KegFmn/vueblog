package com.likc.service.impl;

import com.likc.entity.User;
import com.likc.mapper.UserMapper;
import com.likc.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
