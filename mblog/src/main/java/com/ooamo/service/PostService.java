package com.ooamo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ooamo.vo.PostVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
public interface PostService extends IService<Post> {

    PostVo selectOnePost(QueryWrapper<Post> wrapper);

    void putViewCount(PostVo vo);

    IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    void initWeekRank();

    void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr);

    Integer getPostCount(Long profileId);
}

