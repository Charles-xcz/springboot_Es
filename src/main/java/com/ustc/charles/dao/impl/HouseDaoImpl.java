package com.ustc.charles.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 房屋信息的基本crud
 *
 * @author charles
 * @date 2020/1/21 10:46
 */
@Repository
public class HouseDaoImpl implements HouseDao {
    @Autowired
    private TransportClient client;

    @Override
    public String add(Es es, House house) {
        IndexResponse indexResponse = client.prepareIndex(es.getIndex(), es.getType(), house.getId())
                .setSource(JSON.toJSONString(house), XContentType.JSON).get();
        return indexResponse.getResult().toString();
    }

    @Override
    public String deleteById(Es es, String id) {
        DeleteResponse deleteResponse = client.prepareDelete(es.getIndex(), es.getType(), id).get();
        return deleteResponse.getResult().toString();
    }

    @Override
    public String update(Es es, House house) {
        UpdateResponse updateResponse = client.prepareUpdate(es.getIndex(), es.getType(), house.getId())
                .setDoc(JSON.toJSONString(house), XContentType.JSON).get();
        return updateResponse.getResult().toString();

    }

    @Override
    public Long getCount(Es es) {
        return client.prepareSearch(es.getIndex()).setTypes(es.getType()).execute()
                .actionGet().getHits().getTotalHits();
    }
}
