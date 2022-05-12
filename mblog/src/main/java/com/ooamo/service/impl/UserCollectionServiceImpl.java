package com.ooamo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ooamo.entity.UserCollection;
import com.ooamo.mapper.UserCollectionMapper;
import com.ooamo.service.UserCollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

    @Autowired
    UserCollectionMapper collectionMapper;

    @Override
    public Integer getCollectionCount(Long profileId) {

        return collectionMapper.selectCount(new QueryWrapper<UserCollection>()
                .eq("user_id",profileId));

    }

    @Override
    public void removeByPostId(Long id) {
        collectionMapper.delete(new QueryWrapper<UserCollection>()
                .eq("post_id",id));
    }
}
