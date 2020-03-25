package com.ustc.charles.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;
import com.ustc.charles.util.EsHouseConstant;
import com.ustc.charles.util.EsUtils;
import com.ustc.charles.util.HitsToHousesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/1/21 12:21
 */
@Repository
@Slf4j
public class QueryDaoImpl implements QueryDao {

    @Autowired
    private TransportClient client;
    @Value("${elasticsearch.index}")
    private String index;
    @Value("${elasticsearch.type}")
    private String type;

    @Override
    public List<FieldAttributeDTO> getFieldAttribute() {
        List<FieldAttributeDTO> fieldAttributes = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "houseType"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "layout"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "region"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "floor"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "design"));
        return fieldAttributes;
    }

    @Override
    public List<House> listByPage(Integer currentPage, Integer pageSize) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setFrom((currentPage - 1) * pageSize)
                .setSize(pageSize).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> listByPage(Integer currentPage, Integer pageSize, String sortField) {
        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type);
        if (EsHouseConstant.SORT_DEFAULT.equals(sortField)) {
            requestBuilder.addSort(SortBuilders.scoreSort());
        } else {
            requestBuilder.addSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
        }
        SearchResponse response = requestBuilder.setFrom((currentPage - 1) * pageSize)
                .setSize(pageSize).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryById(String id) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.termQuery("id", id)).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }

    @Override
    public House getById(String id) {
        GetResponse response = client.prepareGet(index, type, id).get();
        return JSON.parseObject(response.getSourceAsString(), House.class);

    }

    @Override
    public List<House> queryByName(String name) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.matchPhraseQuery("name", name)).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }


    @Override
    public List<House> queryByPriceRange(Integer low, Integer high) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.rangeQuery("total_price").gte(low).lte(high)).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryMust(String type, String layout, String region) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("house_type").field("layout").field("region")
                .preTags("<font style='color:red;size=20;'>")
                .postTags("</font>");
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchPhraseQuery("house_type", type))
                        .must(QueryBuilders.matchPhraseQuery("layout", layout))
                        .must(QueryBuilders.matchPhraseQuery("region", region)))
                .highlighter(highlightBuilder).get();
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
        return houses;
    }

    @Override
    public Map<String, Object> searchHouse(QueryParamDTO queryParam, Integer offset, Integer limit) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
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
            qb.must(QueryBuilders.matchPhraseQuery("title", queryParam.getKeyword()))
                    .should(QueryBuilders.matchPhraseQuery("houseType", queryParam.getKeyword()))
                    .should(QueryBuilders.matchPhraseQuery("region", queryParam.getKeyword()))
                    .should(QueryBuilders.matchPhraseQuery("layout", queryParam.getKeyword()));
        }

        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type).setQuery(qb);
        if (queryParam.getSortField() == null || EsHouseConstant.SORT_DEFAULT.equals(queryParam.getSortField())) {
            requestBuilder.addSort(SortBuilders.scoreSort());
        } else {
            requestBuilder.addSort(SortBuilders.fieldSort(queryParam.getSortField()));
        }

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("houseType").field("layout").field("region")
                .preTags("<font style='color:red;size=20;'>")
                .postTags("</font>");

        SearchResponse response = requestBuilder.setFrom(offset).setSize(limit).highlighter(highlightBuilder).get();
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
        Map<String, Object> map = new HashMap<>();
        map.put("houses", houses);
        map.put("totalHits", totalHits);
        return map;
    }

    @Override
    public List<House> querySorted(String field, String content) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.termQuery(field, content))
                .addSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .setFrom(0)
                .setSize(1000).get();
        return HitsToHousesUtil.hitsToBeans(response.getHits());
    }
}
