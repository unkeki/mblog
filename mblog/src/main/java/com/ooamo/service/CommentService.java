package com.ooamo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ooamo.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
public interface CommentService extends IService<Comment> {

    IPage<CommentVo> paging(Page page, Long postId, Long userId, String order);
}
