package com.example.springboot_project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot_project.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 继承MyBatisPlus提供的BaseMapper接口，泛型是自己创建的“User实体类”
 * Repository注解：标记为持久层的接口，相当于将接口的一个实现类交给Spring管理
 */
@Repository
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 自定义的查询方法，具体的SQL语句在“src/main/resources/mapper/UserMapper.xml”中
     * @param name 传入SQL语句的参数
     */
    Map<String, Object> selectMapByName(String name);
}