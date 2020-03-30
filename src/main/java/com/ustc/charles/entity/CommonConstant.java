package com.ustc.charles.entity;

/**
 * @author charles
 * @date 2020/3/24 21:38
 */
public interface CommonConstant {
    String SORT_DEFAULT = "default";
    String SORT_AVG_PRICE = "avg_price";
    String SORT_TOTAL_PRICE = "total_price";
    String SORT_UPDATE_TIME = "update_time";
    /**
     * 记住登录状态:15天
     */
    int REMEMBER_EXPIRED_SECONDS = 15 * 24 * 3600;
    /**
     * 默认登录状态:2h
     */
    int DEFAULT_EXPIRED_SECONDS = 2 * 3600;
    /**
     * 权限:用户
     */
    String AUTHORITY_USER = "user";
    /**
     * 权限:管理员
     */
    String AUTHORITY_ADMIN = "admin";
    /**
     * 权限:版主
     */
    String AUTHORITY_MODERATOR = "moderator";
    /**
     * 异步请求头
     */
    String X_REQUESTED_WITH = "XMLHttpRequest";
}
