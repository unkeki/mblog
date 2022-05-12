package com.ooamo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ooamo.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
@Component
public interface CommentMapper extends BaseMapper<Comment> {

    IPage<CommentVo> selectComments(Page page, @Param(Constants.WRAPPER)QueryWrapper<Comment> orderByDesc);
}
