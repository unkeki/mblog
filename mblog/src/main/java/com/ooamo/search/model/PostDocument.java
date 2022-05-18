package com.ooamo.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(indexName="post")
public class PostDocument implements Serializable {

    @Id
    private Long id;

    // ik分词器
    @Field(type = FieldType.Text, searchAnalyzer="ik_smart", analyzer = "ik_max_word")
    private String title;

    private Long authorId;

    @Field(type = FieldType.Keyword)
    private String authorName;
    private String authorAvatar;

    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;

    private Integer level;
    private Boolean recommend;

    private Integer commentCount;
    private Integer viewCount;

    private Date created;
}
