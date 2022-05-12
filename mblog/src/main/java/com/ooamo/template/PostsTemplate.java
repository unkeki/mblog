package com.ooamo.template;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.common.templates.DirectiveHandler;
import com.ooamo.common.templates.TemplateDirective;
import com.ooamo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//自定义标签实现数据传输，实现置顶文章显示
@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    PostService postService;

    @Override
    public String getName() {
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {

        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn", 1);
        Integer size = handler.getInteger("size");
        Long categoryId = handler.getLong("categoryId");

        IPage page = postService.paging(new Page(pn,size),categoryId,null,level,null,"created");
        handler.put(RESULTS,page).render();

    }
}
