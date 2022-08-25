package com.likc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.likc.mapper.SearchMapper;
import com.likc.search.CollectDoc;
import com.likc.service.SearchService;
import com.likc.vo.SearchBlogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Page<SearchBlogVo> search(String keyword, Pageable page) {

        Criteria criteria = new Criteria();

        criteria = criteria.and(new Criteria("title").matches(keyword))
                            .or(new Criteria("content").matches(keyword));

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria).setPageable(page);

        SearchHits<CollectDoc> searchHits = elasticsearchRestTemplate.search(criteriaQuery, CollectDoc.class);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        List<SearchBlogVo> collectDocs = searchHits.get().map(e ->{
            SearchBlogVo searchBlogVo = new SearchBlogVo();
            searchBlogVo.setId(e.getContent().getId());
            searchBlogVo.setDescription(e.getContent().getDescription());
            searchBlogVo.setTitle(e.getContent().getTitle());
            searchBlogVo.setUpdated(LocalDateTime.parse(e.getContent().getUpdated(), dateTimeFormatter));
            return searchBlogVo;
        }).collect(Collectors.toList());

        Page<SearchBlogVo> docPage = new PageImpl<>(collectDocs, page, searchHits.getTotalHits());

        return docPage;
    }

}
