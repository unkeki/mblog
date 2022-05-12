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
public class UserMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发送消息的用户ID
     */
    private Long fromUserId;

    /**
     * 接收消息的用户ID
     */
    private Long toUserId;

    /**
     * 消息可能关联的帖子
     */
    private Long postId;

    /**
     * 消息可能关联的评论
     */
    private Long commentId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     * 0系统消息 1评论文章 2评论评论
     */
    private Integer type;

    /**
     * 消息状态
     */
    private Integer status;


}
