package com.ooamo.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Post;
import com.ooamo.mapper.PostMapper;
import com.ooamo.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ooamo.util.RedisUtil;
import com.ooamo.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public PostVo selectOnePost(QueryWrapper<Post> wrapper)  {
        return postMapper.selectOnePost(wrapper);
    }

    /**
     * 文章每阅读一次，阅读数加一
     * @param vo
     */
    @Override
    public void putViewCount(PostVo vo) {

        String key = "post:rank:"+vo.getId();

        //1、从缓冲中获取viewCount
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");
        //2、如果没有，就从实体中获取
        if (viewCount != null){
            vo.setViewCount(viewCount+1);
        }else {
            vo.setViewCount(vo.getViewCount()+1);
        }
        //3、同步到缓冲
        redisUtil.hset(key,"post:viewCount",vo.getViewCount());
        //4、同步到数据库
        Post post = postMapper.selectOne(new QueryWrapper<Post>()
                .eq("id", vo.getId()));
        post.setViewCount(vo.getViewCount());
        this.updateById(post);
    }

    /**
     * 首页文章分页
     * @param page
     * @param categoryId
     * @param userId
     * @param level
     * @param recommend
     * @param order
     * @return
     */
    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if(level == null){
            level = -1;
        }
        QueryWrapper<Post> wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);

        return postMapper.selectPosts(page,wrapper);
    }

    /**
     * 每周热议初始化
     */
    @Override
    public void initWeekRank(){

        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7))
                .select("id,title,user_id,comment_count,view_count,created"));

        for(Post post : posts){
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            redisUtil.zSet(key,post.getId(),post.getCommentCount());

            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime =  (7 - between) * 24 * 60 * 60;

            redisUtil.expire(key,expireTime);

            this.hashCachePostIdAndTitle(post,expireTime);
        }
        this.zunionAndStoreLast7DayForWeekRank();

    }

    /**
     * 缓冲文章的基本信息
     * @param post
     * @param expireTime
     */
    private void hashCachePostIdAndTitle(Post post,long expireTime){
//        System.out.println(post);

        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if(!hasKey){
            redisUtil.hset(key,"post:id",post.getId(),expireTime);
            redisUtil.hset(key,"post:title",post.getTitle(),expireTime);
            redisUtil.hset(key,"post:commentCount",post.getCommentCount(),expireTime);
            redisUtil.hset(key,"post:viewCount",post.getViewCount(),expireTime);
        }
    }

    private void zunionAndStoreLast7DayForWeekRank(){

        String currentKey = "day:rank:" + DateUtil.format(new Date(),DatePattern.PURE_DATE_FORMAT);

        String destKey = "week:rank";

        List<String> otherKeys = new ArrayList<>();
        for(int i = -6;i<0;i++){
            String temp  = "day:rank:" + DateUtil.format(DateUtil.offsetDay(new Date(),i),DatePattern.PURE_DATE_FORMAT);
            otherKeys.add(temp);
        }
        redisUtil.zUnionAndStore(currentKey,otherKeys,destKey);
    }

    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr? 1: -1);

        Post post = this.getById(postId);

        // 7天后自动过期(15号发表，7-（18-15）=4)
        long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

        // 缓存这篇文章的基本信息
        this.hashCachePostIdAndTitle(post, expireTime);

        // 重新做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    @Override
    public Integer getPostCount(Long profileId) {
      return postMapper.selectCount(new QueryWrapper<Post>()
                .eq("user_id", profileId));

    }
}
