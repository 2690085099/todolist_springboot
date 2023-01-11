package com.example.springboot_project.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_project.mapper.UserMapper;
import com.example.springboot_project.pojo.User;
import com.example.springboot_project.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Impl的全称为“实现——Implement”
 * ServiceImpl<UserMapper, User>：UserMapper是Mapper接口，User是POJO目录的实体类
 * 并实现了service目录下的UserService接口
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}