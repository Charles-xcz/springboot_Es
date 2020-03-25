package com.ustc.charles;

import com.ustc.charles.dao.EsHouseRepository;
import com.ustc.charles.dao.HouseMapper;
import com.ustc.charles.util.HitsToHousesUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class EsApplicationTests {

    @Resource
    private HouseMapper houseMapper;
    @Autowired
    private EsHouseRepository esHouseRepository;
    @Autowired
    private TransportClient client;
    @Autowired
    private ElasticsearchTemplate template;

    @Test
    void contextLoads() {
    }


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
                .setQuery(QueryBuilders.multiMatchQuery("普通住宅","houseType")).get();
        System.out.println(response.getHits().totalHits);
        System.out.println(response.getHits().getTotalHits());
        System.out.println(HitsToHousesUtil.hitsToBeans(response.getHits()));
    }


}
