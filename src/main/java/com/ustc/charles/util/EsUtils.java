package com.ustc.charles.util;

import com.ustc.charles.dto.FieldAttributeDTO;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
public class EsUtils {
    public static FieldAttributeDTO fieldAggregation(SearchRequestBuilder rb, String field) {
        FieldAttributeDTO fieldAttribute = new FieldAttributeDTO();
        fieldAttribute.setField(field);
        fieldAttribute.setName(FieldToNameUtil.fieldToName.get(field));
        SearchResponse response = rb.addAggregation(AggregationBuilders.terms(field + "Agg").field(field + ".keyword")).get();
        Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
        StringTerms houseTypeTerms = (StringTerms) asMap.get(field + "Agg");
        List<StringTerms.Bucket> buckets = houseTypeTerms.getBuckets();
        List<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            if (!bucket.getKeyAsString().isEmpty()) {
                list.add(bucket.getKeyAsString());
            }
        }
        fieldAttribute.setValues(list);
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
