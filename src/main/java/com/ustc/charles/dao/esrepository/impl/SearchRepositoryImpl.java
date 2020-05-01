package com.ustc.charles.dao.esrepository.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.esrepository.SearchRepository;
import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.util.EsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SortBy;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author charles
 * @date 2020/1/21 12:21
 */
@Slf4j
@Repository
public class SearchRepositoryImpl implements SearchRepository {

    @Autowired
    private TransportClient client;
    @Value("${elasticsearch.index}")
    private String index;
    @Value("${elasticsearch.type}")
    private String type;

    @Override
    public List<FieldAttributeDto> getFieldAttribute(String cityName) {
        List<FieldAttributeDto> fieldAttributes = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        searchRequestBuilder.setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("cityName", cityName)));

        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "region"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "locals"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "community"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "houseType"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "floor"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "layout"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "design"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "decorate"));
        return fieldAttributes;
    }

    @Override
    public ServiceMultiResult<HouseBucketDto> mapAggregate(String cityName) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).
                //暂时还没有城市字段--待添加
                        addAggregation(AggregationBuilders.terms("regionAgg").field("region.keyword"));
        SearchResponse response = searchRequestBuilder.get();
        List<HouseBucketDto> buckets = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            log.warn("Aggregate status is not ok for " + searchRequestBuilder);
            return new ServiceMultiResult<>(buckets, 0);
        }
        Terms terms = response.getAggregations().get("regionAgg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            buckets.add(new HouseBucketDto(bucket.getKeyAsString(), bucket.getDocCount()));
        }
        return new ServiceMultiResult<>(buckets, response.getHits().getTotalHits());
    }

    @Override
    public House getById(String id) {
        GetResponse response = client.prepareGet(index, type, id).get();
        return JSON.parseObject(response.getSourceAsString(), House.class);
    }


    @Override
    public ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, Integer offset, Integer limit) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.filter(QueryBuilders.termQuery("cityName", queryParam.getCityName()));
        /*
        价格条件
         */
        List<String> prices = queryParam.getPrice();
        if (null != prices) {
            BoolQueryBuilder priceQb = QueryBuilders.boolQuery();
            for (String price : prices) {
                String[] split = price.split("-");
                if (Integer.parseInt(split[1]) != 0) {
                    priceQb.should(QueryBuilders.rangeQuery("totalPrice").gte(Integer.parseInt(split[0])).lte(Integer.parseInt(split[1])));
                } else {
                    priceQb.should(QueryBuilders.rangeQuery("totalPrice").gte(Integer.parseInt(split[0])));
                }
            }
            qb.must(priceQb);
        }
        /*
        面积条件
         */
        List<String> areas = queryParam.getArea();
        if (null != areas) {
            BoolQueryBuilder areaQb = QueryBuilders.boolQuery();
            for (String area : areas) {
                String[] split = area.split("-");
                if (Integer.parseInt(split[1]) != 0) {
                    areaQb.should(QueryBuilders.rangeQuery("area").gte(Double.parseDouble(split[0])).lte(Double.parseDouble(split[1])));
                } else {
                    areaQb.should(QueryBuilders.rangeQuery("area").gte(Double.parseDouble(split[0])));
                }
            }
            qb.must(areaQb);
        }

        /*
        其他属性条件
         */
        qb.must(EsUtils.fieldQuery("houseType", queryParam.getHouseType()));
        qb.must(EsUtils.fieldQuery("layout", queryParam.getLayout()));
        qb.must(EsUtils.fieldQuery("floor", queryParam.getFloor()));
        qb.must(EsUtils.fieldQuery("design", queryParam.getDesign()));
        qb.must(EsUtils.fieldQuery("decorate", queryParam.getDecorate()));
        qb.must(EsUtils.fieldQuery("liftProportion", queryParam.getLiftProportion()));
        qb.must(EsUtils.fieldQuery("region", queryParam.getRegion()));
        qb.must(EsUtils.fieldQuery("community", queryParam.getCommunity()));
        /*
        keyword查询
         */
        if (queryParam.getKeyword() != null && !StringUtils.isBlank(queryParam.getKeyword())) {
            qb.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("title", queryParam.getKeyword())))
                    .should(QueryBuilders.matchQuery("houseType", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("community", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("locals", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("region", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("layout", queryParam.getKeyword()));
        }

        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type).setQuery(qb);
        /*
         设置排序
         */
        if (StringUtils.isBlank(orderMode) || CommonConstant.SORT_DEFAULT_DESC.equals(orderMode)) {
            requestBuilder.addSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        } else if (CommonConstant.SORT_DEFAULT_ASC.equals(orderMode)) {
            requestBuilder.addSort(SortBuilders.scoreSort().order(SortOrder.ASC));
        } else {
            String[] split = orderMode.split("-");
            FieldSortBuilder sortBuilder = SortBuilders.fieldSort(split[0]);
            if (split[1].equals("desc")) {
                sortBuilder.order(SortOrder.DESC);
            } else {
                sortBuilder.order(SortOrder.ASC);
            }
            requestBuilder.addSort(sortBuilder).addSort(SortBuilders.scoreSort());
        }
        /*
         设置高亮
         */
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("houseType").field("layout").field("region").field("locals")
                .preTags("<font style='color:red;size=20;'>")
                .postTags("</font>");
        requestBuilder.setFrom(offset).setSize(limit).highlighter(highlightBuilder);

        log.debug(requestBuilder.toString());
        //执行
        SearchResponse response = requestBuilder.get();

        /*
        封装结果
         */
        long totalHits = response.getHits().totalHits;
        List<House> houses = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
            for (Map.Entry<String, HighlightField> fieldEntry : highlightFieldMap.entrySet()) {
                String key = fieldEntry.getKey();
                String value = fieldEntry.getValue().fragments()[0].toString();
                map.put(key, value);
            }
            House house = JSON.parseObject(JSON.toJSONString(map), House.class);
            houses.add(house);
        }
        return new ServiceMultiResult<>(houses, totalHits);
    }

    @Override
    public ServiceMultiResult<House> adminQueryHouse(DatatableSearch searchBody) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        if (searchBody.getCity() != null) {
            qb.filter(QueryBuilders.termQuery("cityName", searchBody.getCity()));
        }
        if (Strings.isNotBlank(searchBody.getTitle())) {
            qb.must(QueryBuilders.matchQuery("title", searchBody.getTitle()));
        }
        Date minTime = searchBody.getCreateTimeMin();
        Date maxTime = searchBody.getCreateTimeMax();
        if (minTime != null && maxTime != null) {
            qb.must(QueryBuilders.rangeQuery("createTime").gte(minTime).lte(maxTime));
        }
        String direction = searchBody.getDirection();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setQuery(qb);
        if ("desc".equals(direction)) {
            searchRequestBuilder.addSort(SortBuilders.fieldSort(searchBody.getOrderBy()).order(SortOrder.DESC));
        } else {
            searchRequestBuilder.addSort(SortBuilders.fieldSort(searchBody.getOrderBy()).order(SortOrder.ASC));
        }
        log.debug(searchRequestBuilder.toString());
        SearchResponse response = searchRequestBuilder.get();
        List<House> houses = EsUtils.hitsToBeans(response.getHits());
        return new ServiceMultiResult<>(houses, response.getHits().totalHits);
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix, String cityName) {

        SearchRequest request = new SearchRequest().indices(index).types(type);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("cityName", cityName))
                .must(QueryBuilders.matchQuery("title", prefix));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQuery).sort(SortBuilders.scoreSort()).from(0).size(5)
                .fetchSource("title", "");

        request.source(searchSourceBuilder);
        List<String> suggests = new ArrayList<>();
        try {
            SearchResponse response = client.search(request).get();
            for (SearchHit hit : response.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                suggests.add((String) sourceAsMap.get("title"));
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return ServiceResult.of(suggests);


//        CompletionSuggestionBuilder suggestion = SuggestBuilders
//                .completionSuggestion("title.suggest")
//                .prefix(prefix).size(10);
//
//        SuggestBuilder suggestBuilder = new SuggestBuilder();
//        suggestBuilder.addSuggestion("autocomplete", suggestion);
//
//        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type)
//                .setQuery(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("cityName", cityName)))
//                .suggest(suggestBuilder);
//
//        log.debug(requestBuilder.toString());
//
//        SearchResponse response = requestBuilder.get();
//        Suggest suggest = response.getSuggest();
//        if (suggest == null) {
//            return ServiceResult.of(new ArrayList<>());
//        }
//        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");
//        int maxSuggest = 0;
//        Set<String> suggestSet = new HashSet<>();
//        for (Object term : result.getEntries()) {
//            if (term instanceof CompletionSuggestion.Entry) {
//                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
//
//                if (item.getOptions().isEmpty()) {
//                    continue;
//                }
//
//                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
//                    String tip = option.getText().string();
//                    if (suggestSet.contains(tip)) {
//                        continue;
//                    }
//                    suggestSet.add(tip);
//                    maxSuggest++;
//                }
//            }
//            if (maxSuggest > 5) {
//                break;
//            }
//        }
//        List<String> suggests = Arrays.asList(suggestSet.toArray(new String[]{}));
    }
}
