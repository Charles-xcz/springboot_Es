package com.ustc.charles.dao.esrepository;

import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;

import java.util.List;

/**
 * 查询
 *
 * @author charles
 * @date 2020/1/21 12:01
 */
public interface SearchRepository {


    ServiceMultiResult<HouseBucketDto> mapAggregate(String cityEnName);

    House getById(String id);

    /**
     * 搜索条件任意满足
     *
     * @param queryParam  搜索条件
     * @param currentPage currentPage
     * @param pageSize    pageSize
     * @return List<House>
     */
    ServiceMultiResult<House> searchHouse(QueryParamDto queryParam, String orderMode, Integer currentPage, Integer pageSize);

    ServiceMultiResult<House> adminQueryHouse(DatatableSearch searchBody);

    /**
     * 获取字段属性
     */
    List<FieldAttributeDto> getFieldAttribute(String cityName);

    ServiceResult<List<String>> suggest(String prefix, String cityName);
}
