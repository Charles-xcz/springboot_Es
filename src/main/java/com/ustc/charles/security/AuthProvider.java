package com.ustc.charles.security;

import com.ustc.charles.model.User;
import com.ustc.charles.service.UserService;
import com.ustc.charles.util.CommonUtil;
import com.ustc.charles.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author charles
 * @date 2020/5/1 12:03
 */
@Slf4j
public class AuthProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!validate(request)) {
            throw new VerifyCodeException("验证码不正确！");
        }
        String userName = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();
        User user = userService.findUserByName(userName);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("用户名不存在!");
        }
        String password = CommonUtil.md5(inputPassword + user.getSalt());

        if (user.getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        throw new BadCredentialsException("密码错误!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    /**
     * 验证保存的验证码和表单提交的验证码是否一致
     */
    private boolean validate(HttpServletRequest request) throws AuthenticationException {
        String kaptchaOwner = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("验证码不正确");
            return false;
        }
        for (Cookie cookie : cookies) {
            if ("kaptchaOwner".equals(cookie.getName())) {
                kaptchaOwner = cookie.getValue();
                break;
            }
        }
        // 检查验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        String code = null;
        try {
            code = ServletRequestUtils.getStringParameter(request, "code");
        } catch (ServletRequestBindingException e) {
            e.printStackTrace();
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            log.warn("验证码不正确");
            return false;
        }
        return true;
    }
}
