package com.ustc.charles.service.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.mapper.UserMapper;
import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.entity.LoginTicket;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.User;
import com.ustc.charles.service.UserService;
import com.ustc.charles.util.CommonUtil;
import com.ustc.charles.util.RedisKeyUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author charles
 * @date 2020/3/26 10:19
 */
@Service
public class UserServiceImpl implements UserService, CommonConstant {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ServiceResult<User> findUserById(int id) {
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return ServiceResult.of(user);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(CommonUtil.generateUUID().substring(0, 5));
        user.setPassword(CommonUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(1);
        user.setActivationCode(CommonUtil.generateUUID());
        user.setHeaderUrl(getRandomHeader());
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        return map;
    }

    /**
     * 调用第三方api生成随机头像
     *
     * @return 头像url
     */
    private String getRandomHeader() {
        String url = "https://api.uomg.com/api/rand.avatar?format=json";
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null) {
                Map<String, String> parse = JSON.parseObject(body.string(), Map.class);
                return parse.get("imgurl");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 2) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证密码
        password = CommonUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommonUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 1.优先从redis缓存中取值
     *
     * @param userId
     * @return
     */
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 2.取不到时初始化缓存数据
     *
     * @param userId
     * @return
     */
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3.数据变更时清除缓存数据
     *
     * @param userId
     */
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId).getResult();
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                if (user.getType() == 1) {
                    return AUTHORITY_ADMIN;
                }
                return AUTHORITY_USER;
            }
        });
        return list;
    }
}
