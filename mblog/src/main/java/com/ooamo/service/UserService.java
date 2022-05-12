package com.ooamo.service;

import com.ooamo.common.lang.Result;
import com.ooamo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ooamo.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
public interface UserService extends IService<User> {

    AccountProfile login(String username, String valueOf);

    Result register(User user);
}
