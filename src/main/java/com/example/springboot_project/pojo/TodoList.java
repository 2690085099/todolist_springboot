package com.example.springboot_project.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 存储用户事项记录的表
 * 使用Redis时，报NotSerializableException错，因为没有实现Serializable接口
 * Serializable是用于实现Java类的序列化操作而提供的一个语义级别的接口（标识用的而已）
 * 实现了Serializable接口，表明TodoList这个类可以被序列化
 */
@Data
@TableName("todo_list")
public class TodoList implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
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