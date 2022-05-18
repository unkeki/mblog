package com.ooamo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.service.*;
import com.ooamo.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserMessageService messageService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserCollectionService collectionService;

    @Autowired
    WsService wsService;

    @Autowired
    SearchService searchService;

    @Autowired
    AmqpTemplate amqpTemplate;


    public Page getPage(){

        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 3);

        return new Page(pn,size);

    }

    protected AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId(){
        return getProfile().getId();
    }
}
