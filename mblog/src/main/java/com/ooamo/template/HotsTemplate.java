package com.ooamo.template;

import com.ooamo.common.templates.DirectiveHandler;
import com.ooamo.common.templates.TemplateDirective;
import com.ooamo.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class HotsTemplate extends TemplateDirective {


    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {

        String weekRankKey = "week:rank";

        //获取排序
        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(weekRankKey, 0, 6);

        List<Map> hotPosts = new ArrayList<>();

        for(ZSetOperations.TypedTuple typedTuple : typedTuples){
            HashMap<Object, Object> map = new HashMap<>();

            Object value = typedTuple.getValue();

            String postKey = "rank:post:"+value;

            map.put("id",value);
            map.put("title",redisUtil.hget(postKey,"post:title"));
            map.put("commentCount",typedTuple.getScore());
//            System.out.println(map);

            hotPosts.add(map);
        }

        handler.put(RESULTS, hotPosts).render();


    }
}
