package com.likc.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */
public interface CollectDocRepository extends ElasticsearchRepository<CollectDoc, Long> {

}
