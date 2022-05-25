package com.likc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.likc.mapper.SearchMapper;
import com.likc.search.CollectDoc;
import com.likc.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */
@Service
public class SearchSeviceImpl extends ServiceImpl<SearchMapper, CollectDoc> implements SearchService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<CollectDoc> search(String keyword, Pageable page) {

        Criteria criteria = new Criteria();

        criteria.and(new Criteria("title").matches(keyword))
                .or(new Criteria("content").matches(keyword))
                .or(new Criteria("typeName").matches(keyword));

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(page);

        SearchHits<CollectDoc> searchHits = elasticsearchRestTemplate.search(criteriaQuery, CollectDoc.class);

        List<CollectDoc> collectDocs = searchHits.get().map(e ->{
            return e.getContent();
        }).collect(Collectors.toList());

        Page<CollectDoc> docPage = new PageImpl<>(collectDocs, page, searchHits.getTotalHits());

        return docPage;
    }

}
