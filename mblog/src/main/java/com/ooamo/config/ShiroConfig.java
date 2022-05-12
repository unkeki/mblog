package com.ooamo.config;

import com.ooamo.shiro.AccountRealm;
import com.ooamo.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);

        log.info("------------------>securityManager注入成功");

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);

//      设置登陆url和登陆成功url
        filterFactoryBean.setLoginUrl("/auth/login");
        filterFactoryBean.setSuccessUrl("/index");
//      配置未授权登陆页面
        filterFactoryBean.setUnauthorizedUrl("/error");

//        配置过滤器
        Map<String,String> hashMap = new LinkedHashMap<>();

        hashMap.put("/res/**", "anon");

//        hashMap.put("/user/home", "auth");
//        hashMap.put("/user/set", "auth");
//        hashMap.put("/user/upload", "auth");
//        hashMap.put("/user/index", "auth");
//        hashMap.put("/user/public", "auth");
//        hashMap.put("/user/collection", "auth");
//        hashMap.put("/user/mess", "auth");
//        hashMap.put("/msg/remove/", "auth");
//        hashMap.put("/message/nums/", "auth");
//
//        hashMap.put("/collection/remove/", "auth");
//        hashMap.put("/collection/find/", "auth");
//        hashMap.put("/collection/add/", "auth");
//
//        hashMap.put("/post/edit", "auth");
//        hashMap.put("/post/submit", "auth");
//        hashMap.put("/post/delete/", "auth");
//        hashMap.put("/post/reply/", "auth");
//        hashMap.put("/post/jieda-delete/", "auth");
//
//        hashMap.put("/websocket", "anon");
//        hashMap.put("/login", "anon");

        filterFactoryBean.setFilterChainDefinitionMap(hashMap);
        return filterFactoryBean;
    }

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
}
