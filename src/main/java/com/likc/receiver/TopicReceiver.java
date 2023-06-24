package com.likc.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.likc.dto.BlogMqDTO;
import com.likc.entity.Blog;
import com.likc.entity.Type;
import com.likc.search.CollectDoc;
import com.likc.search.CollectDocRepository;
import com.likc.service.TypeService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.authenticator.SavedRequest;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.messaging.MessageChannel;
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

    @RabbitListener(queues = "blog")
    public void processSave(Message message, Channel channel) {
        String blogString = new String(message.getBody());
        try {
            BlogMqDTO blogMqDTO = objectMapper.readValue(blogString, BlogMqDTO.class);
            CollectDoc collectDoc = initDoc(blogMqDTO.getBlog());
            if ("edit".equals(blogMqDTO.getType())) {
                ObjectNode filteredNode = excludeNull(collectDoc);
                String filteredDocString = filteredNode.toString();
                UpdateQuery builder = UpdateQuery
                        .builder(String.valueOf(collectDoc.getId()))
                        .withDocument(Document.parse(filteredDocString))
                        .build();
                CollectDoc doc = elasticsearchRestTemplate.get(String.valueOf(collectDoc.getId()), CollectDoc.class);
                if (Objects.isNull(doc)) {
                    elasticsearchRestTemplate.save(collectDoc);
                } else {
                    elasticsearchRestTemplate.update(builder, elasticsearchRestTemplate.getIndexCoordinatesFor(CollectDoc.class));
                }
            } else {
                elasticsearchRestTemplate.delete(String.valueOf(collectDoc.getId()), CollectDoc.class);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("消费失败：{}", e.getMessage());
        }
    }

    private CollectDoc initDoc(Blog blog) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
        return collectDoc;
    }

    private ObjectNode excludeNull(CollectDoc collectDoc) {
        ObjectNode docNode = objectMapper.valueToTree(collectDoc);
        ObjectNode filteredNode = objectMapper.createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> fields = docNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (!entry.getValue().isNull()) {
                filteredNode.set(entry.getKey(), entry.getValue());
            }
        }
        return filteredNode;
    }
}
