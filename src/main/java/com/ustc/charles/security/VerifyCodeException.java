package com.ustc.charles.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author charles
 * @date 2020/5/1 16:05
 */
public class VerifyCodeException extends AuthenticationException {
    public VerifyCodeException(String msg) {
        super(msg);
    }
}
