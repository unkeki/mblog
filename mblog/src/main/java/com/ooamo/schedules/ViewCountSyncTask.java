package com.ooamo.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ooamo.entity.Post;
import com.ooamo.service.PostService;
import com.ooamo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ViewCountSyncTask {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    PostService postService;


    //秒 分 时 天 周 月
    @Scheduled(cron = "* 0/1 * * * *") //每分钟同步
    public void task() {

        Set<String> keys = redisTemplate.keys("post:rank:*");

        List<String> ids = new ArrayList<>();
        for (String key : keys) {
            if(redisUtil.hHasKey(key, "post:viewCount")){
                ids.add(key.substring("post:rank:".length()));
            }
        }
//        System.out.println(ids);

        if(ids.isEmpty()) return;

        // 需要更新阅读量
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        posts.stream().forEach((post) ->{
            Integer viewCount = (Integer) redisUtil.hget("post:rank:" + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });

        if(posts.isEmpty()) return;

        boolean isSucc = postService.updateBatchById(posts);

        if(isSucc) {
            ids.stream().forEach((id) -> {
                redisUtil.hdel("post:rank:" + id, "post:viewCount");
//                System.out.println(id + "---------------------->同步成功");
            });
        }
    }

}
