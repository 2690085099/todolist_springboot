package com.example.springboot_project.tool;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken生成的工具类
 * JWT token的格式：header.payload.signature
 * header的格式（算法、token的类型）,默认：{"alg": "HS512","typ": "JWT"}
 * payload的格式 设置：（用户信息、创建时间、生成时间）
 * signature的生成算法：
 * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */

@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtils {
    //定义token返回头部
    public static String header = "X-Content-Type-Options:nosniff";
    
    //token前缀
    public static String tokenPrefix;
    
    //签名密钥
    public static String secret = "1gHuiop975cdashyex9Ud23ldsvm2Xq";
    
    //有效期
    public static long expireTime = System.currentTimeMillis() + 604800;
    
    //存进客户端的token的key名
    public static final String USER_LOGIN_TOKEN = "USER_LOGIN_TOKEN";
    
    /**
     * 获取令牌的方式
     */
    public static String createToken() {
        Map<String, Object> map = new HashMap<>();
        // 加密算法
        map.put("alg", "HS256");
        // 类型
        map.put("typ", "JWT");
        Date date = new Date();
        // 创建日历对象
        Calendar calendar = Calendar.getInstance();
        // 将日历对象加7天：
        calendar.add(Calendar.DATE, 7);
        
        return JWT.create()
                // 添加头部
                .withHeader(map)
                // 生成签名的时间
                .withIssuedAt(date)
                // 生成签名的有效期（calendar的getTime()方法能将Calendar类型转换为Date类型
                .withExpiresAt(calendar.getTime())
                //生效时间
                .withNotBefore(date)
                .sign(Algorithm.HMAC256(secret));
    }
    
    /**
     * 获取Token的过期时间
     */
    public static Date getTokenExpiresAt(String token) {
        // 对Token进行解码：
        DecodedJWT jwt = JWT.decode(token);
        // 返回Date的比较，前比后，使用compareTo()方法，如果大于0则表示前面的Date时间比较后
        return jwt.getExpiresAt();
    }
    
    
    /**
     * 检查token是否需要更新
     */
    public static boolean isNeedUpdate(String token) {
        //获取token过期时间
        Date expiresAt;
        try {
            expiresAt = JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(token.replace(tokenPrefix, ""))
                    .getExpiresAt();
        } catch (TokenExpiredException e) {
            return true;
        }
        //如果剩余过期时间少于过期时常的一般时 需要更新
        return (expiresAt.getTime() - System.currentTimeMillis()) < (expireTime >> 1);
    }
}