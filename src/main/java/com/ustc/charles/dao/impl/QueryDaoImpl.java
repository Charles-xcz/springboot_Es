package com.ustc.charles.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.QueryParam;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;
import com.ustc.charles.util.HitsToBeanUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/1/21 12:21
 */
@Repository
public class QueryDaoImpl implements QueryDao {

    @Autowired
    private TransportClient client;

    @Override
    public List<House> indexSplitPage(Es es, Integer currentPage, Integer pageSize) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setFrom((currentPage - 1) * pageSize)
                .setSize(pageSize).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> indexSplitPage(Es es, Integer currentPage, Integer pageSize, String field) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .addSort(SortBuilders.fieldSort(field).order(SortOrder.DESC).unmappedType("date"))
                .setFrom((currentPage - 1) * pageSize)
                .setSize(pageSize).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryById(Es es, String id) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.termQuery("id", id)).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryByName(Es es, String name) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.matchPhraseQuery("name", name)).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }


    @Override
    public List<House> queryByPriceRange(Es es, Integer low, Integer high) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.rangeQuery("total_price").gte(low).lte(high)).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryMust(Es es, String type, String layout, String region) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("house_type").field("layout").field("region")
                .preTags("<font style='color:red;size=20;'>")
                .postTags("</font>");
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
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
    public List<House> queryShould(Es es, QueryParam queryParam, Integer currentPage, Integer pageSize) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchPhraseQuery("title", queryParam.getKeyword()))
                .should(QueryBuilders.matchPhraseQuery("name", queryParam.getKeyword()))
                .should(QueryBuilders.matchPhraseQuery("houseType", queryParam.getKeyword()))
                .should(QueryBuilders.matchPhraseQuery("region", queryParam.getKeyword()))
                .should(QueryBuilders.matchPhraseQuery("layout", queryParam.getKeyword()));
        /*
        价格属性的查询
         */
        String[] prices = queryParam.getPrice();
        BoolQueryBuilder priceQb = QueryBuilders.boolQuery();
        if (null != prices) {
            for (String price : prices) {
                String[] split = price.split("-");
                if (Integer.parseInt(split[1]) != 0) {
                    priceQb.should(QueryBuilders.rangeQuery("total_price").gte(Integer.parseInt(split[0])).lte(Integer.parseInt(split[1])));
                } else {
                    priceQb.should(QueryBuilders.rangeQuery("total_price").gte(Integer.parseInt(split[0])));
                }
            }
        }
        qb.must(priceQb);
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(qb)
                .setFrom((currentPage - 1) * pageSize)
                .setSize(pageSize).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> querySorted(Es es, String field, String content) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.termQuery(field, content))
                .addSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .setFrom(0)
                .setSize(1000).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }
}
