package com.ustc.charles.dao.impl;

import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;
import com.ustc.charles.util.HitsToBeanUtil;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author charles
 * @date 2020/1/21 12:21
 */
@Repository
public class QueryDaoImpl implements QueryDao {

    @Autowired
    private TransportClient client;

    @Override
    public List<House> queryByName(Es es, String name) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.matchPhraseQuery("name", name)).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }


    @Override
    public List<House> queryByPriceRange(Es es, Integer low, Integer high) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders
                        .rangeQuery("total_price")
                        .gte(low).lte(high)).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryMust(Es es, String type, String layout, String region) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchPhraseQuery("house_type", type))
                        .must(QueryBuilders.matchPhraseQuery("layout", layout))
                        .must(QueryBuilders.matchPhraseQuery("region", region))).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryShould(Es es, String type, String layout, String region) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType())
                .setQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("house_type", type))
                        .should(QueryBuilders.matchPhraseQuery("layout", layout))
                        .should(QueryBuilders.matchPhraseQuery("region", region))).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());
    }

    @Override
    public List<House> queryAll(Es es) {
        SearchResponse response = client.prepareSearch(es.getIndex()).setTypes(es.getType()).get();
        return HitsToBeanUtil.hitsToBeans(response.getHits());


    }
}
