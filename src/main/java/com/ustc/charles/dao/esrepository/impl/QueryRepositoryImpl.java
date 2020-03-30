package com.ustc.charles.dao.esrepository.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.esrepository.QueryRepository;
import com.ustc.charles.dao.mapper.HouseMapper;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.model.House;
import com.ustc.charles.util.EsUtils;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/1/21 12:21
 */
@Slf4j
@Repository
public class QueryRepositoryImpl implements QueryRepository {

    @Autowired
    private TransportClient client;
    @Value("${elasticsearch.index}")
    private String index;
    @Value("${elasticsearch.type}")
    private String type;
    @Resource
    private HouseMapper houseMapper;

    @Override
    public List<FieldAttributeDto> getFieldAttribute() {
        List<FieldAttributeDto> fieldAttributes = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "houseType"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "layout"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "region"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "floor"));
        fieldAttributes.add(EsUtils.fieldAggregation(searchRequestBuilder, "design"));
        return fieldAttributes;
    }


    @Override
    public List<House> listByPage(Integer currentPage, Integer pageSize, String sortField) {
        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type);
        if (CommonConstant.SORT_DEFAULT.equals(sortField)) {
            requestBuilder.addSort(SortBuilders.scoreSort());
        } else {
            requestBuilder.addSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
        }
        requestBuilder.setFrom((currentPage - 1) * pageSize).setSize(pageSize);

        log.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        return EsUtils.hitsToBeans(response.getHits());
    }


    @Override
    public House getById(String id) {
        GetResponse response = client.prepareGet(index, type, id).get();
        return JSON.parseObject(response.getSourceAsString(), House.class);
    }


    @Override
    public ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, Integer offset, Integer limit) {
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
            qb.must(QueryBuilders.matchQuery("title", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("houseType", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("region", queryParam.getKeyword()))
                    .should(QueryBuilders.matchQuery("layout", queryParam.getKeyword()));
        }

        SearchRequestBuilder requestBuilder = client.prepareSearch(index).setTypes(type).setQuery(qb);
        /*
         设置排序
         */
        if (StringUtils.isBlank(orderMode) || CommonConstant.SORT_DEFAULT.equals(orderMode)) {
            requestBuilder.addSort(SortBuilders.scoreSort());
        } else {
            requestBuilder.addSort(SortBuilders.fieldSort(orderMode).order(SortOrder.DESC)).addSort(SortBuilders.scoreSort());
        }
        /*
         设置高亮
         */
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("houseType").field("layout").field("region")
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
        return new ServiceMultiResult<>(EsUtils.hitsToBeans(response.getHits()), totalHits);
    }
}
