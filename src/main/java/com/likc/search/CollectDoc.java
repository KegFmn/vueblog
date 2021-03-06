package com.likc.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author likc
 * @date 2022/5/25
 * @description
 */
@Data
@Document(indexName = "vueblog_collect", createIndex = true)
public class CollectDoc {

    @Id
    private Long id;

    @Field(name = "typeId", type = FieldType.Long)
    private Long typeId;

    @Field(name = "typeName", type = FieldType.Text)
    private String typeName;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String content;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssXXX")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "MMM d HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS||HH:mm:ss||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||strict_date_optional_time||epoch_millis")
    private String updated;


}
