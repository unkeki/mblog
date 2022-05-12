package com.ooamo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ooamo.entity.Category;
import com.ooamo.service.CategoryService;
import com.ooamo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * 项目运行时自动进行配置
 */
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    CategoryService categoryService;

    ServletContext servletContext;

    @Autowired
    PostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //获取分类栏
        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0));

        servletContext.setAttribute("categories",categories);

        postService.initWeekRank();

    }

    @Override
    public void setServletContext(ServletContext servletContext) {

        this.servletContext = servletContext;

    }
}
