package com.ustc.charles.model;

import com.ustc.charles.entity.CommonConstant;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/26 10:13
 */
@Data
public class User implements Serializable, UserDetails, CommonConstant {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String phoneNumber;
    private Date lastLoginTime;
    private Date lastUpdateTime;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                if (getType() == 1) {
                    return AUTHORITY_ADMIN;
                }
                return AUTHORITY_USER;
            }
        });
        return list;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status == 1;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}
