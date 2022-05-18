package com.ooamo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ooamo.entity.Post;
import com.ooamo.search.model.PostDocument;
import com.ooamo.search.mq.PostMqIndexMessage;
import com.ooamo.search.repository.PostRepository;
import com.ooamo.service.PostService;
import com.ooamo.service.SearchService;
import com.ooamo.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PostService postService;

    @Override
    public List<PostVo> search(String keyword) {
        // 分页信息 mybatis plus的page 转成 jpa的page
//        Long current = page.getCurrent()-1;
//        Long size = page.getSize();
//        Pageable pageable = PageRequest.of(current.intValue(), size.intValue());

        // 搜索es得到pageData
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,
                "title", "authorName", "categoryName");

//        org.springframework.data.domain.Page<PostDocument> docments = postRepository.search(multiMatchQueryBuilder, pageable);
        Iterable<PostDocument> documents = postRepository.search(multiMatchQueryBuilder);
        List<PostVo> pageData = new ArrayList<>();
        for(PostDocument document : documents){
            PostVo postVo = modelMapper.map(document, PostVo.class);
            pageData.add(postVo);
        }
        // 结果信息 jpa的pageData转成mybatis plus的pageData
//        IPage pageData = new Page(page.getCurrent(), page.getSize(), docments.getTotalElements());
//        pageData.setRecords(docments.getContent());
//        System.out.println("pageData:"+pageData);
        return pageData;
    }

    @Override
    public int initEsData(List<PostVo> records) {
        if(records == null || records.isEmpty()) {
            return 0;
        }

        List<PostDocument> documents = new ArrayList<>();
        for(PostVo vo : records) {
            // 映射转换
            PostDocument PostDocument = modelMapper.map(vo, PostDocument.class);
            documents.add(PostDocument);
        }
        postRepository.saveAll(documents);
        return documents.size();
    }

    @Override
    public void createOrUpdateIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();
        PostVo postVo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", postId));

        PostDocument PostDocument = modelMapper.map(postVo, PostDocument.class);

        postRepository.save(PostDocument);

        log.info("es 索引更新成功！ ---> {}", PostDocument.toString());
    }

    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);
        log.info("es 索引删除成功！ ---> {}", message.toString());
    }
}
