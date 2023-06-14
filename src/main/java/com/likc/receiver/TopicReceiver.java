package com.likc.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.search.CollectDoc;
import com.likc.search.CollectDocRepository;
import com.likc.service.TypeService;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class TopicReceiver {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private TypeService typeService;

    @RabbitListener(queues = "blog.save")
    public void processSave(Blog blog) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (Objects.nonNull(blog.getId())) {
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
            // 排除null
            ObjectNode docNode = objectMapper.valueToTree(collectDoc);
            ObjectNode filteredNode = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = docNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (!entry.getValue().isNull()) {
                    filteredNode.set(entry.getKey(), entry.getValue());
                }
            }
            String filteredDocString = filteredNode.toString();
            UpdateQuery builder = UpdateQuery
                        .builder(String.valueOf(collectDoc.getId()))
                        .withDocument(Document.parse(filteredDocString))
                        .build();
            elasticsearchRestTemplate.update(builder, elasticsearchRestTemplate.getIndexCoordinatesFor(CollectDoc.class));
        } else {
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
        }
    }

    @RabbitListener(queues = "blog.delete")
    public void processDelete(Long id) {
        elasticsearchRestTemplate.delete(String.valueOf(id), CollectDoc.class);
    }

}
