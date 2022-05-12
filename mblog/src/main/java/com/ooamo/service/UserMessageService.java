package com.ooamo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ooamo.vo.UserMessageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
public interface UserMessageService extends IService<UserMessage> {


    IPage<UserMessageVo> paging(Page page, QueryWrapper<UserMessage> wrapper);

    void updateToReaded(List<Long> ids);
}
