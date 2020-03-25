package com.ustc.charles.dao;

import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;

import java.util.List;
import java.util.Map;

/**
 * 复合查询
 *
 * @author charles
 * @date 2020/1/21 12:01
 */
public interface QueryDao {

    /**
     * 分页查询所有房屋信息,默认排序
     *
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @return List<House>
     */
    List<House> listByPage(Integer currentPage, Integer pageSize);

    /**
     * 分页查询所有房屋信息,根据字段field排序
     *
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @param sortMode    排序字段
     * @return List<House>
     */
    List<House> listByPage(Integer currentPage, Integer pageSize, String sortMode);

    /**
     * 根据房屋id查询
     *
     * @param id id
     * @return List<House>
     */
    List<House> queryById(String id);

    House getById(String id);

    /**
     * 根据房屋name查询
     *
     * @param name 房屋名
     * @return List<House>
     */
    List<House> queryByName(String name);

    /**
     * 根据价格范围搜索
     *
     * @param low  价格下限
     * @param high 价格上限
     * @return List<House>
     */
    List<House> queryByPriceRange(Integer low, Integer high);

    /**
     * 搜索条件全部满足
     *
     * @param type   houseType
     * @param layout layout
     * @param region region
     * @return List<House>
     */
    List<House> queryMust(String type, String layout, String region);

    /**
     * 搜索条件任意满足
     *
     * @param queryParam  搜索条件
     * @param currentPage currentPage
     * @param pageSize    pageSize
     * @return List<House>
     */
    Map<String, Object> searchHouse(QueryParamDTO queryParam, Integer currentPage, Integer pageSize);

    /**
     * 排序查询,依据得分降序排列
     *
     * @param field   检索字段
     * @param content 检索内容
     * @return List<House>
     */
    List<House> querySorted(String field, String content);

    /**
     * 获取字段属性
     *
     * @return List<FieldAttribute>
     */
    List<FieldAttributeDTO> getFieldAttribute();
}
