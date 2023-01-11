package com.example.springboot_project.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot_project.mapper.TodoListMapper;
import com.example.springboot_project.pojo.TodoList;
import com.example.springboot_project.service.TodoListService;
import org.springframework.stereotype.Service;

@Service
public class TodoListServiceImpl extends ServiceImpl<TodoListMapper, TodoList> implements TodoListService {
}