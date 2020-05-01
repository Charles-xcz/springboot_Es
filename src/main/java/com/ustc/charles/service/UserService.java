package com.ustc.charles.service;

import com.ustc.charles.entity.LoginTicket;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author charles
 * @date 2020/4/23 14:24
 */
public interface UserService {
    ServiceResult<User> findUserById(int id);

    Map<String, Object> register(User user);

    Map<String, Object> login(String username, String password, long expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    User findUserByName(String username);

    int updateHeader(int userId, String headerUrl);
}
