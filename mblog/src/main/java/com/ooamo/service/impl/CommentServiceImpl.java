package com.ooamo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Comment;
import com.ooamo.mapper.CommentMapper;
import com.ooamo.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ooamo.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, Long userId, String order) {

        return commentMapper.selectComments(page,new QueryWrapper<Comment>()
                            .eq(postId != null,"post_id",postId)
                            .eq(userId != null,"user_id",userId)
                            .orderByDesc(order != null,order));
    }
}
