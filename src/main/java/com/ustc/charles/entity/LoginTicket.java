package com.ustc.charles.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author charles
 * @date 2020/3/26 10:22
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
