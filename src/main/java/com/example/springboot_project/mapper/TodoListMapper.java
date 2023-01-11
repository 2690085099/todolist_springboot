package com.example.springboot_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_project.pojo.TodoList;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TodoListMapper extends BaseMapper<TodoList> {

}