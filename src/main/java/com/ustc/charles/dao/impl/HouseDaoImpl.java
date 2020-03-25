package com.ustc.charles.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.model.House;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * 房屋信息的基本crud
 *
 * @author charles
 * @date 2020/1/21 10:46
 */
@Deprecated
@Repository
public class HouseDaoImpl implements HouseDao {
    @Autowired
    private TransportClient client;
    @Value("${elasticsearch.index}")
    private String index;
    @Value("${elasticsearch.type}")
    private String type;

    @Override
    public String add(House house) {
        IndexResponse indexResponse = client.prepareIndex(index, type, house.getId())
                .setSource(JSON.toJSONString(house), XContentType.JSON).get();
        return indexResponse.getResult().toString();
    }

    @Override
    public String deleteById(String id) {
        DeleteResponse deleteResponse = client.prepareDelete(index, type, id).get();
        return deleteResponse.getResult().toString();
    }

    @Override
    public String update(House house) {
        UpdateResponse updateResponse = client.prepareUpdate(index, type, house.getId())
                .setDoc(JSON.toJSONString(house), XContentType.JSON).get();
        return updateResponse.getResult().toString();

    }

    @Override
    public Long getCount() {
        return client.prepareSearch(index).setTypes(type).execute()
                .actionGet().getHits().getTotalHits();
    }

}
