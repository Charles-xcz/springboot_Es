package com.ustc.charles.util;

import com.alibaba.fastjson.JSONObject;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.model.House;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/2/18 17:18
 */
@Slf4j
public class EsUtils {
    /**
     * 将搜索内容封装为List<House>
     *
     * @param hits 搜索命中
     * @return List<House>
     */
    public static List<House> hitsToBeans(SearchHits hits) {
        List<House> houses = new ArrayList<>();
        for (SearchHit hit : hits) {
            House house = JSONObject.parseObject(hit.getSourceAsString(), House.class);
            houses.add(house);
        }
        return houses;
    }

    public static FieldAttributeDto fieldAggregation(SearchRequestBuilder rb, String field) {
        FieldAttributeDto fieldAttribute = new FieldAttributeDto();
        fieldAttribute.setField(field);
        fieldAttribute.setName(FieldAttributeDto.FIELD_TO_NAME.get(field));
        SearchRequestBuilder requestBuilder = rb.addAggregation(AggregationBuilders.terms(field + "Agg").field(field + ".keyword"));
        log.debug(requestBuilder.toString());
        SearchResponse response = requestBuilder.get();
        Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
        StringTerms houseTypeTerms = (StringTerms) asMap.get(field + "Agg");
        List<StringTerms.Bucket> buckets = houseTypeTerms.getBuckets();
        List<HouseBucketDto> list = new ArrayList<>();
        int count = 0;
        for (StringTerms.Bucket bucket : buckets) {
            String asString = bucket.getKeyAsString();
            if (!StringUtils.isBlank(asString) && !asString.equals("暂无数据")) {
                list.add(new HouseBucketDto(asString, bucket.getDocCount()));
                count++;
            }
            if (count >= 5) {
                break;
            }
        }
        fieldAttribute.setHouseBuckets(list);
        return fieldAttribute;
    }

    public static BoolQueryBuilder fieldQuery(String fieldName, List<String> values) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (values != null) {
            for (String value : values) {
                qb.should(QueryBuilders.termQuery(fieldName + ".keyword", value));
            }
        }
        return qb;
    }
}
