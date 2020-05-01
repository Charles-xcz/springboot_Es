package com.ustc.charles.config;

import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.security.AuthProvider;
import com.ustc.charles.security.RedisRememberMeRepo;
import com.ustc.charles.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @author charles
 * @date 2020/3/26 20:29
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommonConstant {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private RedisRememberMeRepo rememberMeRepo;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                //授权:基于权限的授权,更灵活(建议使用)
                .antMatchers("/user/setting")
                .hasAnyAuthority(AUTHORITY_ADMIN, AUTHORITY_USER)
                .antMatchers("/admin/**")
                .hasAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()
                .and()
                .rememberMe()
                .tokenRepository(rememberMeRepo)
                .rememberMeCookieName("my-token")
                .tokenValiditySeconds(3600 * 24 * 5)
                .userDetailsService(userDetailsService)
                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");

        http.formLogin().loginPage("/login").loginProcessingUrl("/login").failureUrl("/login?error");
        http.logout().logoutUrl("/logout");
        http.headers().frameOptions().disable();

        http.sessionManagement()
                // 无效session跳转
                .invalidSessionUrl("/login")
                .maximumSessions(1)
                // session过期跳转
                .expiredUrl("/login")
                .sessionRegistry(sessionRegistry());
    }

    /**
     * 自定义认证策略
     */
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider()).eraseCredentials(true);
    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

//    @Bean
//    public LoginUrlEntryPoint urlEntryPoint() {
//        return new LoginUrlEntryPoint("/user/login");
//    }

//    @Bean
//    public LoginAuthFailHandler authFailHandler() {
//        return new LoginAuthFailHandler(urlEntryPoint());
//    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() {
        AuthenticationManager authenticationManager = null;
        try {
            authenticationManager = super.authenticationManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationManager;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 注册bean sessionRegistry
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
