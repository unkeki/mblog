package com.ooamo.vo;

import com.ooamo.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * EqualsAndHashCode注解详解
 *
 *此注解会生成equals(Object other) 和 hashCode()方法。
 *
 * 它默认使用非静态，非瞬态的属性
 *
 * 可通过参数exclude排除一些属性
 *
 * 可通过参数of指定仅使用哪些属性
 *
 * 它默认仅使用该类中定义的属性且不调用父类的方法
 *
 * 可通过callSuper=true解决上一点问题。让其生成的方法中调用父类的方法。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostVo extends Post {

    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String categoryName;

}
