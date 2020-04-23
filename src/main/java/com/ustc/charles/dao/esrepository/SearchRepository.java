package com.ustc.charles.dao.esrepository;

import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
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

    /**
     * 分页查询所有房屋信息,根据字段field排序
     *
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @param sortMode    排序字段
     * @return List<House>
     */
    List<House> listByPage(Integer currentPage, Integer pageSize, String sortMode);


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


    /**
     * 获取字段属性
     *
     * @return List<FieldAttribute>
     */
    List<FieldAttributeDto> getFieldAttribute();
}
