package com.ooamo.shiro;

import com.ooamo.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//shiro连接数据
@Component
public class AccountRealm extends AuthorizingRealm{

    @Autowired
    UserService userService;

//    授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        AccountProfile profile = (AccountProfile) principals.getPrimaryPrincipal();

        // 给id为6的admin赋予admin角色
        if(profile.getId() == 1) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("admin");
            return info;
        }

        return null;
    }

//    认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        AccountProfile profile = userService.login(usernamePasswordToken.getUsername(), String.valueOf( usernamePasswordToken.getPassword()));

        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
        return info;
    }
}
