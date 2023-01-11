package com.example.springboot_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot_project.pojo.User;
import org.springframework.stereotype.Repository;

/**
 * service：业务层/服务层
 * Repository注解：标记为持久层的接口，相当于将接口的一个实现类交给Spring管理
 * User为实体类
 * IServices是一个接口规范，接口中包含的一系列的Mapper(DAO)层交互操作，一般在服务层（Service）进行继承操作
 */
@Repository
public interface UserService extends IService<User> {
}