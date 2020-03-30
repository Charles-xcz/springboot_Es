package com.ustc.charles.service;

import com.ustc.charles.dao.esrepository.EsHouseRepository;
import com.ustc.charles.dao.esrepository.QueryRepository;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.model.House;
import com.ustc.charles.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author charles
 * @date 2020/3/25 9:34
 */
@Service
@Slf4j
public class EsHouseService {
    @Autowired
    private EsHouseRepository esHouseRepository;
    @Autowired
    private QueryRepository queryRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    public House findById(String id) {
        return queryRepository.getById(id);
    }

    public void save(House house) {
        esHouseRepository.save(house);
    }

    public void saveAll(List<House> houses) {
        esHouseRepository.saveAll(houses);
    }

    public void deleteById(Long houseId) {
        esHouseRepository.deleteById(Math.toIntExact(houseId));
    }

    public long getCount() {
        return esHouseRepository.count();
    }

    public List<House> listByPage(int current, int limit, String sortField) {
        return queryRepository.listByPage(current, limit, sortField);
    }

    public ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, int offset, int limit) {
        return queryRepository.searchHouse(queryParam, orderMode, offset, limit);
    }

    public ServiceMultiResult<FieldAttributeDto> getFieldAttributes(String city) {
        String key = RedisKeyUtil.getFieldAggKey(city);
        List<FieldAttributeDto> fieldAttributes = redisTemplate.opsForList().range(key, 0, -1);

        if (fieldAttributes != null && fieldAttributes.size() != 0) {
            log.debug("属性聚合:{},", fieldAttributes.size());
            return new ServiceMultiResult<>(fieldAttributes, fieldAttributes.size());
        }
        fieldAttributes = queryRepository.getFieldAttribute();

        redisTemplate.opsForList().rightPushAll(key, fieldAttributes);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
        return new ServiceMultiResult<>(fieldAttributes, fieldAttributes.size());
    }
}
