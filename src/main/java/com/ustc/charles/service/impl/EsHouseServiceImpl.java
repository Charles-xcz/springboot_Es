package com.ustc.charles.service.impl;

import com.ustc.charles.dao.esrepository.EsHouseRepository;
import com.ustc.charles.dao.esrepository.SearchRepository;
import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.service.EsHouseService;
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
public class EsHouseServiceImpl implements EsHouseService {
    @Autowired
    private EsHouseRepository esHouseRepository;
    @Autowired
    private SearchRepository searchRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public House findById(String id) {
        return searchRepository.getById(id);
    }

    @Override
    public void save(House house) {
        esHouseRepository.save(house);
    }

    @Override
    public void saveAll(List<House> houses) {
        esHouseRepository.saveAll(houses);
    }

    @Override
    public void deleteById(Long houseId) {
        esHouseRepository.deleteById(Math.toIntExact(houseId));
    }

    @Override
    public long getCount() {
        return esHouseRepository.count();
    }

    @Override
    public ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, int offset, int limit) {
        return searchRepository.searchHouse(queryParam, orderMode, offset, limit);
    }

    @Override
    public ServiceMultiResult<House> adminQuery(DatatableSearch searchBody) {
        return searchRepository.adminQueryHouse(searchBody);
    }

    @Override
    public ServiceMultiResult<FieldAttributeDto> getFieldAttributes(String cityName) {
        String key = RedisKeyUtil.getFieldAggKey(cityName);
//        List<FieldAttributeDto> fieldAttributes = redisTemplate.opsForList().range(key, 0, -1);
//
//        if (fieldAttributes != null && fieldAttributes.size() != 0) {
//            log.debug("属性聚合:{},", fieldAttributes.size());
//            return new ServiceMultiResult<>(fieldAttributes, fieldAttributes.size());
//        }
        List<FieldAttributeDto> fieldAttributes = searchRepository.getFieldAttribute(cityName);
//        redisTemplate.opsForList().rightPushAll(key, fieldAttributes);
//        redisTemplate.expire(key, 3, TimeUnit.HOURS);
        return new ServiceMultiResult<>(fieldAttributes, fieldAttributes.size());
    }

    @Override
    public ServiceMultiResult<HouseBucketDto> mapAggregate(String cityEnName) {
        return searchRepository.mapAggregate(cityEnName);
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix, String cityName) {
        return searchRepository.suggest(prefix, cityName);
    }
}
