package com.example.springboot_project.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.springboot_project.mapper.TodoListMapper;
import com.example.springboot_project.mapper.UserMapper;
import com.example.springboot_project.pojo.TodoList;
import com.example.springboot_project.pojo.User;
import com.example.springboot_project.tool.EmailUtil;
import com.example.springboot_project.tool.JWTUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * RestController注解：表示这个类用于开发接口，属于控制器
 */
@RestController
@RequestMapping("/TodoList")
public class TodoListController {
    /**
     * user表：
     * UserMapper是DAO层的接口
     */
    private final UserMapper userMapper;
    
    /**
     * todo_list表：
     */
    private final TodoListMapper todoListMapper;
    
    /**
     * 注入发送邮件的接口
     */
    private final EmailUtil mailService;
    
    public TodoListController(UserMapper userMapper, TodoListMapper todoListMapper, EmailUtil mailService) {
        this.userMapper = userMapper;
        this.todoListMapper = todoListMapper;
        this.mailService = mailService;
    }
    
    /**
     * 注册
     * RequestBody注解 User user——用于接收前端传来的数据，User是实体类
     * user需要传入的参数有“name（姓名）”和“password（密码）”
     */
    @RequestMapping("/register")
    public String register(@RequestBody User user) {
        Long userCount = userMapper.selectCount(
                // 根据前端传来的name的值去查找记录，并列出name字段
                new QueryWrapper<User>().eq("name", user.getName()).select("name")
        );
        
        if (userCount == 0) {
            // 插入一条记录
            userMapper.insert(user);
            return "注册成功";
        } else {
            return "用户名重复，请更换用户名！";
        }
    }
    
    /**
     * 发送邮件
     *
     * @param request 包含用户的邮箱、注册验证码
     */
    @RequestMapping("/postEmail")
    public String sendThymeleafMail(@RequestBody JSONObject request) {
        try {
            // 发送包含验证码的邮件
            mailService.sendThymeleafMail(request.get("email").toString(),
                    request.get("verificationCode").toString());
            return "验证码已发送到您的邮箱！";
        } catch (Exception e) {
            return "验证码获取失败···";
        }
    }
    
    /**
     * 游客登录要用到的匿名注册和顺便登录的接口
     * user需要传入的参数：name（姓名）
     */
    @RequestMapping("/loginAnonymously")
    @Cacheable("loginAnonymously")
    public JSONObject loginAnonymously(@RequestBody User user) {
        // 创建一个JSON对象
        JSONObject resultJson = new JSONObject();
        
        try {
            // 查询user表并输出为List集合
            List<User> userInformation = userMapper.selectList(
                    // 使用QueryWrapper自定义查询，查询"name"和"avatar"两个字段，条件为前端传入的name值，eq表示“=”
                    new QueryWrapper<User>().eq("name", user.getName()).select("name", "avatar")
            );
            Map<String, Object> selectMap = new HashMap<>();
            for (User userRecord : userInformation) {
                // 将用户的头像提取出到一个map集合中
                selectMap.put("name", userRecord.getName());
                selectMap.put("avatar", userRecord.getAvatar());
            }
            
            // 判断是否存在此用户，如果不存在，则开始注册，否则就不用注册
            if (selectMap.get("name") == null) {
                userMapper.insert(user);
            }
            
            // JWTUtils是自己写的JTW工具类，这里调用“创建Token”的静态方法：
            resultJson.put("jwt", JWTUtils.createToken());
            // 获取map集合中的头像数据，并添加到JSONObject中：
            resultJson.put("avatar", selectMap.get("avatar"));
            resultJson.put("result", "登录成功");
        } catch (Exception e) {
            resultJson.put("result", "抱歉，您的设备不支持游客登录···");
        }
        
        return resultJson;
    }
    
    /**
     * QQ授权后，将信息存储到后端
     */
    @RequestMapping("/qqLogin")
    public void qqLogin(@RequestBody User user) {
        List<User> userInformation = userMapper.selectList(
                // 使用QueryWrapper自定义查询，查询"name"和"avatar"两个字段，条件为前端传入的name值
                new QueryWrapper<User>().eq("name", user.getName()).select("name", "avatar")
        );
        Map<String, Object> selectMap = new HashMap<>();
        for (User userRecord : userInformation) {
            // 将用户的头像提取出到一个map集合中
            selectMap.put("name", userRecord.getName());
            selectMap.put("avatar", userRecord.getAvatar());
        }
        // 判断是否存在此用户，如果不存在，则开始注册，否则就不用注册
        if (selectMap.get("name") == null) {
            userMapper.insert(user);
        }
    }
    
    /**
     * 用户登录的方法
     */
    @RequestMapping("/login")
    @Cacheable("login")
    public JSONObject login(@RequestBody User user) {
        JSONObject resultJson = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 查询用户后返回的记录条数，若是0则无记录
        Long userCount = userMapper.selectCount(
                // 根据前端传来的name的值去查找记录，并列出name字段
                queryWrapper.eq("name", user.getName()).select("name")
        );
        
        // 判断是否存在此用户，如果不存在，则返回不存在此用户
        if (userCount == 0) {
            resultJson.put("result", "不存在此用户，请检查账号是否输入正确！");
        } else {
            // 根据传入的name和password，查询user表的记录
            List<User> selectUserInformation = userMapper.selectList(
                    // 两个eq()作为条件的键和值，中间默认加and()，如果要or()就得手动加
                    queryWrapper.eq("name", user.getName()).eq("password", user.getPassword())
                            // 注意！这里如果不指定字段，会按照上面select()里的“name”参数继续在这里使用
                            .select("name", "password", "avatar")
            );
            Map<String, Object> selectMap = new HashMap<>();
            for (User userRecord : selectUserInformation) {
                // 将用户的头像提取出到一个map集合中
                selectMap.put("name", userRecord.getName());
                selectMap.put("password", userRecord.getPassword());
                selectMap.put("userAvatarData", userRecord.getAvatar());
            }
            
            // 对记录进行校验（注意！字符串的比较需要用equals()方法而不是“==”）
            if (user.getName().equals(selectMap.get("name")) && user.getPassword().equals(selectMap.get("password"))) {
                // 账号和密码正确则返回以下信息：
                resultJson.put("jwt", JWTUtils.createToken());
                resultJson.put("result", "登录成功");
                // 由于历史原因，前端的键名要求是“userAvatarData”而不是“avatar”
                resultJson.put("userAvatarData", selectMap.get("userAvatarData"));
            } else {
                resultJson.put("msg", "用户名或密码错误!");
            }
        }
        
        return resultJson;
    }
    
    /**
     * 验证Token
     * RequestHeader注解：指定HTTP请求头的键，后面的参数用来接收键对应的值
     */
    @RequestMapping("/verification")
    @Cacheable("verification")
    public JSONObject verification(@RequestBody User user, @RequestHeader("token") String token) {
        JSONObject resultJson = new JSONObject();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 如果现在的时间比Token的过期时间后，说明Token过期了
        // getTokenExpiresAt()返回Date的比较，前比后，使用compareTo()方法，如果大于0则表示前面的Date时间比较后
        if (new Date().compareTo(JWTUtils.getTokenExpiresAt(token)) > 0) {
            resultJson.put("message", "登录已过期，请重新登录！");
        } else {
            List<User> selectUserInformation = userMapper.selectList(
                    queryWrapper.eq("name", user.getName()).select("avatar")
            );
            // 如果查询到头像的值
            if (userMapper.selectCount(queryWrapper) != 0) {
                Map<String, Object> selectMap = new HashMap<>();
                for (User userRecord : selectUserInformation) {
                    // 将用户的头像提取出到一个map集合中
                    selectMap.put("avatar", userRecord.getAvatar());
                }
                // 返回用户头像数据
                resultJson.put("avatar", selectMap.get("avatar"));
            }
            // 如果没查询到头像的值
            else {
                // 如果没查找到记录，则返回头像数据为null
                resultJson.put("avatar", null);
            }
        }
        
        return resultJson;
    }
    
    /**
     * 获取事项数据
     */
    @RequestMapping("/getObjectArray")
    public List<TodoList> getObjectArray(@RequestBody User user) {
        Map<String, Object> conditionMap = new HashMap<>();
        // 添加条件
        conditionMap.put("name", user.getName());
        // 以map集合里的键值对作为条件，进行查询并返回
        return todoListMapper.selectByMap(conditionMap);
    }
    
    /**
     * 添加事项
     * todoList参数包括：name、mission、done
     */
    @RequestMapping("/addData")
    public Object addData(@RequestBody TodoList todoList) {
        // 添加数据到用户表中：
        todoListMapper.insert(todoList);
        
        // 根据名字查询事项表中记录的时间，并返回时间
        List<TodoList> selectTodoListInformation = todoListMapper.selectList(
                new QueryWrapper<TodoList>().eq("name", todoList.getName()).select("time")
        );
        Map<String, Object> selectMap = new HashMap<>();
        if (selectTodoListInformation != null) {
            for (TodoList todoListRecord : selectTodoListInformation) {
                // 将用户的头像提取出到一个map集合中
                selectMap.put("time", todoListRecord.getTime());
            }
        }
        
        // 返回时间
        return selectMap.get("time");
    }
    
    /**
     * 删除事项
     *
     * @param todoList：todo_list表的参数——name和mission
     */
    @RequestMapping("/deleteData")
    public void deleteData(@RequestBody TodoList todoList) {
        // 删除指定用户的指定记录，使用两个条件，从而不会删错用户的信息
        todoListMapper.delete(
                new QueryWrapper<TodoList>()
                        .eq("name", todoList.getName()).eq("mission", todoList.getMission())
        );
    }
    
    /**
     * 清除已完成的事项
     *
     * @param idArrays：要删除的事项的id数组
     */
    @RequestMapping("/clearCompletedTodo")
    public void clearCompletedTodo(@RequestBody ArrayList<Long> idArrays) {
        todoListMapper.deleteBatchIds(idArrays);
    }
    
    /**
     * 修改事项内容的方法
     *
     * @param request:传入参数有“姓名name”、“事项内容mission”、“修改前的内容beforeContent”、“目前的完成情况nowDone”
     */
    @RequestMapping("/modificationData")
    public void modificationData(@RequestBody JSONObject request) {
        UpdateWrapper<TodoList> updateWrapper = new UpdateWrapper<>();
        
        // 如果是修改事项的内容：
        if (request.get("beforeContent") != null) {
            // 以用户名和修改前的内容作为条件
            updateWrapper.eq("name", request.get("name")).eq("mission", request.get("beforeContent"))
                    // 修改事项内容
                    .set("mission", request.get("mission"));
            // 因为上面已经set()过一个字段了，所以下面update()的第一个参数可以为null：
            todoListMapper.update(null, updateWrapper);
        }
        // 如果是要修改事项的完成状态：
        else if (request.get("nowDone") != null) {
            updateWrapper.eq("name", request.get("name")).eq("mission", request.get("mission"))
                    // 修改事项内容
                    .set("done", request.get("nowDone"));
            todoListMapper.update(null, updateWrapper);
        }
        
    }
    
    /**
     * 完成所有的待办事项
     */
    @RequestMapping("/finishAllTodo")
    public void finishAllTodo(@RequestBody TodoList todoList) {
        // 查找该用户下的所有未完成的事项，将其更新为完成
        todoListMapper.update(null, new UpdateWrapper<TodoList>()
                // done的值：0为false（未完成），1为true（完成）
                .eq("name", todoList.getName()).eq("done", 0).set("done", 1));
    }
    
    /**
     * 取消完成所有待办事项
     */
    @RequestMapping("/noFinishAllTodo")
    public void noFinishAllTodo(@RequestBody TodoList todoList) {
        // 查找该用户下的所有未完成的事项，将其更新为完成
        todoListMapper.update(null, new UpdateWrapper<TodoList>()
                // done的值：0为false（未完成），1为true（完成）
                .eq("name", todoList.getName()).eq("done", 1).set("done", 0));
    }
    
    /**
     * 返回用户上次登录的时间，以及这次登录时间
     */
    @RequestMapping("/updateLoginTime")
    public JSONObject updateLoginTime(@RequestBody User user) {
        JSONObject resultJson = new JSONObject();
        // 格式为年月日、时分秒
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 查询用户上次登录的时间
        List<User> selectUserInformation = userMapper.selectList(
                new QueryWrapper<User>().eq("name", user.getName()).select("login_time")
        );
        Map<String, Object> selectMap = new HashMap<>();
        for (User userRecord : selectUserInformation) {
            // 将格式化后的时间添加到map集合中，format()后面是时间数据
            selectMap.put("login_time", simpleDateFormat.format(userRecord.getLoginTime()));
        }
        // 更新为当前时间
        String date = simpleDateFormat.format(new Date());
        userMapper.update(null, new UpdateWrapper<User>()
                // done的值：0为false（未完成），1为true（完成）
                .eq("name", user.getName()).set("login_time", date));
        
        resultJson.put("上次登录时间", selectMap.get("login_time"));
        resultJson.put("本次登录时间", date);
        
        return resultJson;
    }
    
    /**
     * 重置密码的方法
     * 传入user的参数有：name、password
     */
    @RequestMapping("/resetPassword")
    public String resetPassword(@RequestBody User user) {
        // 查询用户信息
        List<User> selectUserInformation = userMapper.selectList(
                new QueryWrapper<User>().eq("name", user.getName()).select("password")
        );
        Map<String, Object> selectMap = new HashMap<>();
        for (User userRecord : selectUserInformation) {
            // 将格式化后的时间添加到map集合中，format()后面是时间数据
            selectMap.put("password", userRecord.getPassword());
        }
        
        // 如果不存在此用户
        if (selectUserInformation.size() == 0) {
            return "不存在此用户，请检查账号是否输入正确！";
        }
        // 如果存在此用户
        else {
            // 先查询用户原来的密码，看新密码和旧密码是否一致，一致则提示不必修改
            if (user.getPassword().equals(selectMap.get("password"))) {
                return "新密码和旧密码一致，不必修改！";
            }
            // 如果不一样
            else {
                // 则更新用户的密码
                userMapper.update(null, new UpdateWrapper<User>()
                        .eq("name", user.getName()).set("password", user.getPassword()));
                return "密码重置成功！";
            }
        }
    }
    
    /**
     * 用户上传头像的方法
     */
    @RequestMapping("/uploadAvatar")
    public String uploadAvatar(@RequestBody JSONObject request) {
        userMapper.update(null, new UpdateWrapper<User>()
                // 更新指定用户的头像，以Base64编码存储
                .eq("name", request.get("userName")).set("avatar", request.get("base64")));
        
        return "头像设置成功！";
    }
    
    /**
     * @return 返回时间戳
     */
    @RequestMapping("/getDate")
    public long getDate() {
        // 要除以1000才是Unix时间戳：
        return System.currentTimeMillis() / 1000;
    }
    
    /**
     * 提供给前端，用于测试网络的连通性
     */
    @RequestMapping("/ping")
    public String ping() {
        return "网络正常";
    }
}