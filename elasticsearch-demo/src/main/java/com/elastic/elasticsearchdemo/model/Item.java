package com.elastic.elasticsearchdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@Document(indexName = "items_index")
@Setting(settingPath = "static/es-settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @Id
    private String id;

    @Field(value = "name", type = FieldType.Text,analyzer = "custom_index", searchAnalyzer = "custom_search")
    private String name;
    @Field(value = "price", type = FieldType.Double)
    private Double price;
    @Field(value = "brand", type = FieldType.Text,analyzer = "custom_index", searchAnalyzer = "custom_search")
    private String brand;
    @Field(value = "category", type = FieldType.Keyword)
    private String category;
}
