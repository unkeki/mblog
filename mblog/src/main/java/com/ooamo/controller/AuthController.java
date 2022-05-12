package com.ooamo.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.ooamo.common.lang.Result;
import com.ooamo.entity.User;
import com.ooamo.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class AuthController extends BaseController{

    @GetMapping("/login")
    public String login(){
        return "/auth/login";
    }

    /**
     * shiro实现登陆
     * @param email
     * @param password
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String email, String password){

        if(StrUtil.isEmpty(email) || StrUtil.isEmpty(password)){
            return Result.fail("邮箱或密码不能为空！");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));

        try {
            SecurityUtils.getSubject().login(token);

            User user = userService.getById(getProfileId());
            Integer count = postService.getPostCount(getProfileId());
            Integer colCount = collectionService.getCollectionCount(getProfileId());
            user.setPostCount(count);
            user.setCollectionCount(colCount);
            userService.updateById(user);

        } catch (AuthenticationException e) {
            if(e instanceof UnknownAccountException){
                return Result.fail("用户不存在");
            }else if(e instanceof LockedAccountException){
                return Result.fail("用户被禁用");
            }else if(e instanceof IncorrectCredentialsException){
                return Result.fail("密码错误");
            }else {
                return Result.fail("用户认证失败");
            }
        }
        return Result.success().action("/");
    }

    /**
     * 验证码
     */
    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

    @GetMapping("/capthca.jpg")
    public void Kaptcha(HttpServletResponse resp) throws IOException {

        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        req.getSession().setAttribute(KAPTCHA_SESSION_KEY,text);

        resp.setHeader("Cache-Control","no-store, no-cache");
        resp.setContentType("image/jpg");
        ServletOutputStream outputStream = resp.getOutputStream();
        ImageIO.write(image,"jpg",outputStream);

    }


    /**
     * 注册
     * @return
     */
    @GetMapping("/register")
    public String register(){
        return "auth/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user,String repass,String vercode){

        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if(validResult.hasErrors()){
            return Result.fail(validResult.getErrors());
        }
        if(!user.getPassword().equals(repass)){
            return Result.fail("两次输入密码不一样");
        }

        String capthca = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if(vercode == null || !vercode.equalsIgnoreCase(capthca)){
            return Result.fail("验证码输入不正确");
        }

//        完成注册
        Result result = userService.register(user);
        return result.action("/login");
    }


    /**
     * 注销
     * @return
     */
    @RequestMapping("/user/logout/")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }


}
