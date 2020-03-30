package com.ustc.charles;

import com.ustc.charles.model.House;
import com.ustc.charles.util.EsUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/26 15:41
 */
@SpringBootTest
public class QueryTest {
    @Autowired
    private TransportClient client;

    @Test
    void test1() {
        SearchRequestBuilder requestBuilder = client.prepareSearch("es_house").setTypes("_doc");
        String text = "上学方便";
        SearchResponse response = requestBuilder.setQuery(QueryBuilders.matchQuery("title", text)).get();

        List<House> houses = EsUtils.hitsToBeans(response.getHits());
        houses.forEach(System.out::println);
        System.out.println(response.getHits().totalHits);
    }
}
