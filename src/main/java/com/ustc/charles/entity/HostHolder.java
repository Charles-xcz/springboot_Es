package com.ustc.charles.entity;

import com.ustc.charles.model.User;
import org.springframework.stereotype.Component;

/**
 * @author charles
 * @date 2020/3/26 10:12
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
