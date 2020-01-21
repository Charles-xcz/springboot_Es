package com.ustc.charles.util;

import com.alibaba.fastjson.JSONObject;
import com.ustc.charles.model.House;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类:将搜索内容封装为List<House>
 * @author charles
 * @date 2020/1/21 13:48
 */
public class HitsToBeanUtil {
    public static List<House> hitsToBeans(SearchHits hits) {
        List<House> houses = new ArrayList<>();
        for (SearchHit hit : hits) {
            House house = JSONObject.parseObject(hit.getSourceAsString(), House.class);
            houses.add(house);
        }
        return houses;
    }
}
