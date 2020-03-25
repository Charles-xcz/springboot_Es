package com.ustc.charles.dao;

import com.ustc.charles.model.House;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author charles
 * @date 2020/3/25 8:57
 */
@Repository
public interface EsHouseRepository extends ElasticsearchRepository<House, Integer> {
}
