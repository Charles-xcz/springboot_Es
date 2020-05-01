package com.ustc.charles.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 开启Redis会话管理
 * @author charles
 * @date 2020/5/1 9:48
 */
@Configuration
@EnableRedisHttpSession
public class RedisSessionManager {
}
