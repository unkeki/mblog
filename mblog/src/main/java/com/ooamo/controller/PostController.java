package com.ooamo.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ooamo.common.lang.Result;
import com.ooamo.config.RabbitConfig;
import com.ooamo.entity.*;
import com.ooamo.search.mq.PostMqIndexMessage;
import com.ooamo.service.UserService;
import com.ooamo.util.ValidationUtil;
import com.ooamo.vo.CommentVo;
import com.ooamo.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.ooamo.controller.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */

/**
 * 踩坑：RestController和Controller，一般使用Controller，不然return会直接输出字符串
 * 有输出再加上responseBody，加Body的时候就不要return url了，return Result不然也只会输出字符串
 */
@Controller
public class PostController extends BaseController {

    /**
     * 文章分类
     * @param id 使用字符匹配，防止id混乱识别
     * @return
     */
    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") long id){

        //获取前端数据，当前分页，使用ServletRequestUtils可以设置默认值
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);

        req.setAttribute("pn",pn);
        req.setAttribute("currentCategoryId",id);

        return "post/category";
    }


    /**
     * 显示帖子详细信息
     * @param id
     * @return
     */
    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id")long id){

        PostVo vo = postService.selectOnePost(new QueryWrapper<Post>()
                    .eq("p.id",id));
        req.setAttribute("currentCategoryId",vo.getCategoryId());
        req.setAttribute("post",vo);

        Assert.notNull(vo,"文章已被删除");

        //更新文章阅读数量
        postService.putViewCount(vo);

        //获取评论信息
        //分页信息 文章 用户 排序
        IPage<CommentVo> results = commentService.paging(getPage(),vo.getId(),null,"created");
        req.setAttribute("pageData",results);

        return "post/detail";
    }

    /**
     * 编辑帖子
     * @return
     */
    @GetMapping("/post/edit")
    public String edit(){
        String id = req.getParameter("id");
        if(!StringUtils.isEmpty(id)){
            Post post = postService.getById(id);
            Assert.isTrue(post!=null,"该文章已被删除！");
            Assert.isTrue(post.getUserId().longValue()==getProfileId().longValue(),"不好意思，你没有权限编辑此文章");
            req.setAttribute("post",post);
        }
        //分类栏数据
        req.setAttribute("categories",categoryService.list());

        return "post/edit";
    }

    /**
     * 提交帖子
      * @param post
     * @return
     */
    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post){

        //验证post
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if(validResult.hasErrors()){
            return Result.fail(validResult.getErrors());
        }

        //编写新帖子
        if(post.getId() == null){
            post.setUserId(getProfileId());

            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);

            //更新用户文章数量
            User user = userService.getById(getProfileId());
            user.setPostCount(user.getPostCount()+1);
            userService.updateById(user);
        }
        else{
            //修改帖子
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(),"您没有权限编辑此文章");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(post);
        }

        // 通知消息给mq，告知更新或添加
        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE));

        return Result.success().action("/post/"+post.getId());

    }


    /**
     * 删除帖子
     * @param id
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/post/delete/")
    public Result delete(Long id){

        Post post = postService.getById(id);
        Assert.notNull(post,"该文章已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(),"您没有权限编辑此文章");

        postService.removeById(id);

        //更新用户文章数量
        User user = userService.getById(getProfileId());
        user.setPostCount(user.getPostCount()-1);
        userService.updateById(user);

        messageService.removeByMap(MapUtil.of("post_id",id));
        collectionService.removeByMap(MapUtil.of("post_id",id));

        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success().action("/user/index");

    }


    /**
     * 回复
     * @param jid
     * @param content
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid,String content){

        Assert.notNull(jid,"找不到对应的文章");
        Assert.hasLength(content,"评论内容不能为空");

        Post post = postService.getById(jid);
        Assert.isTrue(post!=null,"改文章已被删除");

        Comment comment = new Comment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        comment.setCreated(new Date());
        comment.setModified(new Date());
        commentService.save(comment);

        post.setCommentCount(post.getCommentCount()+1);
        postService.updateById(post);

        //本周热议数量加一
        postService.incrCommentCountAndUnionForWeekRank(post.getId(),true);

        if(comment.getId().longValue() != post.getUserId().longValue()){
            UserMessage userMessage = new UserMessage();
            userMessage.setPostId(jid);
            userMessage.setCommentId(comment.getId());
            userMessage.setFromUserId(getProfileId());
            userMessage.setToUserId(post.getUserId());
            userMessage.setType(1);
            userMessage.setContent(content);
            userMessage.setCreated(new Date());
            userMessage.setStatus(0);
            messageService.save(userMessage);

            wsService.sendMessCountToUser(userMessage.getToUserId());
        }
        if(content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
//            System.out.println(username);

            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if(user != null) {
                UserMessage message = new UserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0);
                messageService.save(message);

                // 即时通知被@的用户
            }
        }
        return Result.success().action("/post/" + post.getId());
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result reply(Long id) {

        Assert.notNull(id, "评论id不能为空！");

        Comment comment = commentService.getById(id);

        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.saveOrUpdate(post);

        //评论数量减一
        postService.incrCommentCountAndUnionForWeekRank(comment.getPostId(), false);

        return Result.success(null);
    }


    /**
     * 收藏数量
      * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid){

        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success(MapUtil.of("collection",count>0));

    }


    /**
     * 添加收藏
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid){

        Post post = postService.getById(pid);
        Assert.isTrue(post != null,"该文章已被删除");

        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        if(count > 0){
            return Result.fail("你已经收藏");
        }



        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());

        collection.setPostUserId(post.getUserId());

        collectionService.save(collection);

        User user = userService.getById(getProfileId());
        user.setCollectionCount(user.getCollectionCount()+1);
        userService.updateById(user);

        return Result.success();

    }

    /**
     * 删除收藏
     * @param pid
     * @return
     */
    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid){

        Post post = postService.getById(pid);

        Assert.isTrue(post != null,"该文章已被删除");

        collectionService.remove(new QueryWrapper<UserCollection>()
        .eq("user_id",getProfileId())
        .eq("post_id",pid));

        User user = userService.getById(getProfileId());
        user.setCollectionCount(user.getCollectionCount()-1);
        userService.updateById(user);

        return Result.success();

    }


}
