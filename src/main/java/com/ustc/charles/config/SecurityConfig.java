package com.ustc.charles.config;

import com.ustc.charles.security.LoginUrlEntryPoint;
import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.util.CommonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.io.PrintWriter;

/**
 * @author charles
 * @date 2020/3/26 20:29
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommonConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                //授权:基于权限的授权,更灵活(建议使用)
                .antMatchers("/user/setting", "/user/upload",
                        "/discuss/add", "/comment/add/**", "/letter/**",
                        "/notice/**", "/like", "/follow", "/unfollow")
                .hasAnyAuthority(AUTHORITY_ADMIN, AUTHORITY_MODERATOR, AUTHORITY_USER)
                .antMatchers("/discuss/top", "/discuss/wonderful")
                .hasAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete", "/data/**", "actuator/**")
                .hasAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");

        //设了一个不存在的登出路径,为了不使用spring security的登出而执行自己完成的登出逻辑
        http.logout().logoutUrl("/securityLogout");
        http.headers().frameOptions().disable();
        //处理
        http.exceptionHandling()
                //没有登录的处理
                .authenticationEntryPoint((request, response, e) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if (X_REQUESTED_WITH.equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommonUtil.getJsonString(403, "你还没有登录"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/login");
                    }
                })
                //权限不足的处理的
                .accessDeniedHandler((request, response, e) -> {
                    String xRequestedWith = request.getHeader("x-requested-with");
                    if (X_REQUESTED_WITH.equals(xRequestedWith)) {
                        response.setContentType("application/plain;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(CommonUtil.getJsonString(403, "你没有访问权限!"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/403");
                    }
                });
    }

    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/user/login");
    }
}
