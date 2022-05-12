package com.ooamo.vo;

import com.ooamo.entity.UserMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserMessageVo extends UserMessage {

    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;
}
