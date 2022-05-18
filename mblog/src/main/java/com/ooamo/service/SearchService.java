package com.ooamo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.search.mq.PostMqIndexMessage;
import com.ooamo.vo.PostVo;

import java.util.List;

public interface SearchService {

    //搜索功能
    List<PostVo> search(String keyword);

    //同步数据
    int initEsData(List<PostVo> records);

    //创建或更新索引
    void createOrUpdateIndex(PostMqIndexMessage message);

    //移除索引
    void removeIndex(PostMqIndexMessage message);
}
