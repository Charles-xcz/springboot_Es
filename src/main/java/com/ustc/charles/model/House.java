package com.ustc.charles.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author charles
 * @date 2020/1/20 16:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "es_house", type = "_doc", createIndex = false)
public class House {
    @Id
    private Long id;
    private Long houseId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Keyword)
    private String url;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String houseType;
    @Field(type = FieldType.Keyword)
    private String positions;
    @Field(type = FieldType.Keyword)
    private String longitude;
    @Field(type = FieldType.Keyword)
    private String latitude;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String layout;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String floor;
    @Field(type = FieldType.Double)
    private Double area;
    @Field(type = FieldType.Text)
    private String cityName;
    @Field(type = FieldType.Keyword)
    private String design;
    private String direction;
    @Field(type = FieldType.Keyword)
    private String decorate;
    @Field(type = FieldType.Keyword)
    private String lift;
    @Field(type = FieldType.Keyword)
    private String liftProportion;
    @Field(type = FieldType.Double)
    private Double totalPrice;
    @Field(type = FieldType.Double)
    private Double avgPrice;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String region;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String locals;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String community;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Date)
    private Date updateTime;
    @Field(type = FieldType.Text)
    private String registerTime;
    private String status;
}
