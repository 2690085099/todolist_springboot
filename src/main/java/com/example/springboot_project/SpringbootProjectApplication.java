package com.example.springboot_project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * SpringBoot框架的主类，本质上是Spring的一个组件
 * SpringBootApplication注解：SpringBoot应用
 * EnableJpaAuditing注解：Spring Data Jpa用的
 * MapperScan注解——映射器扫描：填入Mapper接口的路径，扫描Mapper接口所在的包
 */
@SpringBootApplication
@MapperScan("com.example.springboot_project.mapper")
// 开启缓存
@EnableCaching
public class SpringbootProjectApplication {
    /**
     * 程序运行入口
     */
    public static void main(String[] args) {
        // 运行SpringBoot应用
        SpringApplication.run(SpringbootProjectApplication.class, args);
    }
}