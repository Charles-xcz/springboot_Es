package com.ustc.charles.service;

import com.ustc.charles.EsApplicationTests;
import com.ustc.charles.dao.esrepository.EsHouseRepository;
import com.ustc.charles.dao.mapper.HouseMapper;
import com.ustc.charles.util.EsUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/26 22:32
 */
public class EsServiceTest extends EsApplicationTests {
    @Resource
    private HouseMapper houseMapper;
    @Autowired
    private EsHouseRepository esHouseRepository;
    @Autowired
    private TransportClient client;
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    void testSaveAll() {
        esHouseRepository.saveAll(houseMapper.selectHouses());
    }

    @Test
    void testGetMapping() {
        System.out.println(template.getMapping("es_house", "_doc"));
    }

    @Test
    void testAgg() {
        SearchResponse response = client.prepareSearch("es_house").setTypes("_doc").addAggregation(AggregationBuilders.terms("houseTypeAgg").field("layout.keyword")).get();
        StringTerms houseTypeAgg = (StringTerms) response.getAggregations().getAsMap().get("houseTypeAgg");
        List<StringTerms.Bucket> buckets = houseTypeAgg.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
        });
    }

    @Test
    void testSearch() {
        SearchResponse response = client.prepareSearch("es_house").setTypes("_doc")
                .setQuery(QueryBuilders.multiMatchQuery("普通住宅", "houseType")).get();
        System.out.println(response.getHits().totalHits);
        System.out.println(response.getHits().getTotalHits());
        System.out.println(EsUtils.hitsToBeans(response.getHits()));
    }
}
