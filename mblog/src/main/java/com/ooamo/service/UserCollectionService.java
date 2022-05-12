package com.ooamo.service;

import com.ooamo.entity.UserCollection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
public interface UserCollectionService extends IService<UserCollection> {

    Integer getCollectionCount(Long profileId);

    void removeByPostId(Long id);
}
