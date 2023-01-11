package com.example.springboot_project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制器里面可以写接口，@RequestMapping("/myTest")表示这个类的地址
 */
@RestController
@RequestMapping("/myTest")
public class MyTest {
    /**
     * 接口地址，需要以“地址/myTest/myApi”访问
     * @return 返回给前端的数据
     */
    @RequestMapping("/myApi")
    public String myApi() {
        return "我的第一个SpringBoot接口！";
    }
    
    /**
     * 需要以“某个地址/myTest/twoApi”访问
     */
    @GetMapping("/twoApi")
    @ResponseBody
    public String twoApi() {
        return "第二个API";
    }
}