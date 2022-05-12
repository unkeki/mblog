package com.ooamo.vo;

import com.ooamo.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVo extends Comment {

    private Long authorId;
    private String authorName;
    private String authorAvatar;
}
