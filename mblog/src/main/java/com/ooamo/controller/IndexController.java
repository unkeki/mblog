package com.ooamo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ooamo.shiro.AccountProfile;
import com.ooamo.vo.PostVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController extends BaseController{

    @RequestMapping({"","/","index"})
    public String index(){

        //设置首页分类id为0
        req.setAttribute("currentCategoryId",0);

        //获取首页文章信息
        //分页信息 分类 用户 置顶 精选 排序
        IPage<PostVo> results = postService.paging(getPage(), null, null, null, null, "created");
        req.setAttribute("pageData",results);

        AccountProfile profile = getProfile();
        SecurityUtils.getSubject().getSession().setAttribute("profile",profile);

        return "index";
    }

    @RequestMapping("/search")
    public String search(String q){

        req.setAttribute("currentCategoryId",0);

        List<PostVo> pageData = searchService.search(q);

        req.setAttribute("q",q);
        req.setAttribute("pageData",pageData);
        req.setAttribute("total",pageData.size());

        return "search";
    }

}
