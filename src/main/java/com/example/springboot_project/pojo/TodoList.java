package com.example.springboot_project.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 存储用户事项记录的表
 */
@Data
@TableName("todo_list")
public class TodoList {
    @TableId("id")
    private Long id;
    @TableField("name")
    private String name;
    @TableField("mission")
    private String mission;
    @TableField("done")
    private int done;
    @TableField("time")
    private String time;
    
}