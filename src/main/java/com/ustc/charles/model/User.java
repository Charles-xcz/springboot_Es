package com.ustc.charles.model;

import lombok.Data;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/26 10:13
 */
@Data
public class User {
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

}
