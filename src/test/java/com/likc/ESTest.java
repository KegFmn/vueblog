package com.likc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.receiver.TopicReceiver;
import com.likc.search.CollectDoc;
import com.likc.service.BlogService;
import com.likc.service.TypeService;
import com.likc.vo.SearchBlogVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
public class ESTest {

    @Resource
    private TopicReceiver topicReceiver;
    @Resource
    private BlogService blogService;

    @Resource
    private TypeService typeService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    public void process() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Blog blog = blogService.lambdaQuery().orderByDesc(Blog::getId).last("limit 1").one();
//        for (Blog blog : list) {
            CollectDoc collectDoc = new CollectDoc();
            collectDoc.setId(blog.getId());
            if (Objects.nonNull(blog.getTypeId())) {
                Type type = typeService.lambdaQuery().eq(Type::getId, blog.getTypeId()).one();
                collectDoc.setTypeId(type.getId());
                collectDoc.setTypeName(type.getTypeName());
            }
            collectDoc.setDescription(blog.getDescription());
            collectDoc.setTitle(blog.getTitle());
            collectDoc.setContent(blog.getContent());
            collectDoc.setUpdated(dateTimeFormatter.format(blog.getUpdated()));
            elasticsearchRestTemplate.save(collectDoc);
//        }
//        CollectDoc doc = elasticsearchRestTemplate.save(String.valueOf(blog.getId()), CollectDoc.class);
//
//        CollectDoc collectDoc = new CollectDoc();
//        collectDoc.setId(blog.getId());
//        if (Objects.nonNull(blog.getTypeId())) {
//            Type type = typeService.lambdaQuery().eq(Type::getId, blog.getTypeId()).one();
//            collectDoc.setTypeId(type.getId());
//            collectDoc.setTypeName(type.getTypeName());
//        }
//        collectDoc.setDescription(blog.getDescription());
//        collectDoc.setTitle(blog.getTitle());
//        collectDoc.setContent(blog.getContent());
//        collectDoc.setUpdated(dateTimeFormatter.format(blog.getUpdated()));
//        // 排除null
//        ObjectNode docNode = objectMapper.valueToTree(collectDoc);
//        ObjectNode filteredNode = objectMapper.createObjectNode();
//        Iterator<Map.Entry<String, JsonNode>> fields = docNode.fields();
//        while (fields.hasNext()) {
//            Map.Entry<String, JsonNode> entry = fields.next();
//            if (!entry.getValue().isNull()) {
//                filteredNode.set(entry.getKey(), entry.getValue());
//            }
//        }
//        String filteredDocString = filteredNode.toString();
//        UpdateQuery builder = UpdateQuery
//                .builder(String.valueOf(collectDoc.getId()))
//                .withDocument(Document.parse(filteredDocString))
//                .build();
//        elasticsearchRestTemplate.update(builder, elasticsearchRestTemplate.getIndexCoordinatesFor(CollectDoc.class));
    }

    private Pageable getPage(Integer currentPage, Integer pageSize) {
        return PageRequest.of(currentPage - 1, pageSize,
                Sort.by(Sort.Order.desc("updated")));
    }
}
