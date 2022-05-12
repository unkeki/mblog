package com.ooamo.config;

import com.jagregory.shiro.freemarker.ShiroTags;
import com.ooamo.template.HotsTemplate;
import com.ooamo.template.PostsTemplate;
import com.ooamo.template.TimeAgoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 模板配置
 */
@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    PostsTemplate postsTemplate;

    @Autowired
    HotsTemplate hotsTemplate;

    @PostConstruct
    public void setUp(){

        configuration.setSharedVariable("shiro",new ShiroTags());
        configuration.setSharedVariable("posts", postsTemplate);
        configuration.setSharedVariable("timeAgo",new TimeAgoMethod());
        configuration.setSharedVariable("hots",hotsTemplate);

    }
}
