package com.ustc.charles.service;

import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;

import java.util.List;

/**
 * @author charles
 * @date 2020/4/23 14:30
 */
public interface EsHouseService {
    House findById(String id);

    void save(House house);

    void saveAll(List<House> houses);

    void deleteById(Long houseId);

    long getCount();

    ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, int offset, int limit);

    ServiceMultiResult<FieldAttributeDto> getFieldAttributes(String city);

    ServiceMultiResult<HouseBucketDto> mapAggregate(String cityEnName);

    ServiceResult<List<String>> suggest(String prefix);
}
