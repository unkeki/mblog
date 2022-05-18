package com.ooamo.search.repository;

import com.ooamo.search.model.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchRepository<PostDocument,Long> {

    // 符合jpa命名规范的接口


}
