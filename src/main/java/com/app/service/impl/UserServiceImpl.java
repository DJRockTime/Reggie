package com.app.service.impl;

import com.app.entiry.User;
import com.app.mapper.UserMapper;
import com.app.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
