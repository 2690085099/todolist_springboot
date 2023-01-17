package com.example.springboot_project.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * POJO（简单java对象Plain Old Java Object）
 * User是实体类，尽量和数据库的表的名字对应相同
 * NoArgsConstructor注解：能够自动添加无参数构造函数——public user(){}
 * AllArgsConstructor注解：能够自动添加全参数构造函数
 * Data注解是由”Lombok“提供的
 * 而@Data注解能够代替：NoArgsConstructor、Getter、Setter、EqualsAndHashCode，少一个全参数构造
 * TableName注解：表名
 */
@Data
@TableName(value = "user")
public class User {
    
    /**
     * TableId：表示以这个ID字段作为主键，value值指的是表中的主键的名称
     * 如果加上“type = IdType.AUTO”参数，则主键的值不使用雪花算法生成，而是根据数据库的主键自增（数据库也要设置）
     * type默认是IdType.ASSIGN_ID，与数据库id自增无关
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    /**
     * TableField：数据库的字段名称
     */
    @TableField(value = "name")
    private String name;
    
    @TableField(value = "password")
    private String password;
    
    /**
     * 这里一定要注意！变量名不要使用下划线分割，而是用驼峰命名法，否则识别不了，取值之后为null
     */
    @TableField(value = "login_time")
    private Timestamp loginTime;
    
    @TableField(value = "avatar")
    private String avatar;
}