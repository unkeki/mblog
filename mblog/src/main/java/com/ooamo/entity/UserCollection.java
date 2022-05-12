package com.ooamo.entity;

import com.ooamo.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 文章Id
     */
    private Long postId;

    /**
     * 收藏文章的用户Id，用于通知文章作者
     */
    private Long postUserId;


}
