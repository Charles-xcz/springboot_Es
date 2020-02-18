package com.ustc.charles.dao;

import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;

import java.util.List;

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
     * @param es          index,type
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @return List<House>
     */
    List<House> indexSplitPage(Es es, Integer currentPage, Integer pageSize);

    /**
     * 分页查询所有房屋信息,根据字段field排序
     *
     * @param es          index,type
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @param field       排序字段
     * @return List<House>
     */
    List<House> indexSplitPage(Es es, Integer currentPage, Integer pageSize, String field);

    /**
     * 根据房屋id查询
     *
     * @param es es
     * @param id id
     * @return List<House>
     */
    List<House> queryById(Es es, String id);

    /**
     * 根据房屋name查询
     *
     * @param es   es
     * @param name 房屋名
     * @return List<House>
     */
    List<House> queryByName(Es es, String name);

    /**
     * 根据价格范围搜索
     *
     * @param es   es
     * @param low  价格下限
     * @param high 价格上限
     * @return List<House>
     */
    List<House> queryByPriceRange(Es es, Integer low, Integer high);

    /**
     * 搜索条件全部满足
     *
     * @param es     es
     * @param type   houseType
     * @param layout layout
     * @param region region
     * @return List<House>
     */
    List<House> queryMust(Es es, String type, String layout, String region);

    /**
     * 搜索条件任意满足
     *
     * @param es      es
     * @param queryParam 搜索条件
     * @param currentPage currentPage
     * @param pageSize pageSize
     * @return List<House>
     */
    List<House> queryShould(Es es, QueryParamDTO queryParam, Integer currentPage, Integer pageSize);

    /**
     * 排序查询,依据得分降序排列
     *
     * @param es      index,type
     * @param field   检索字段
     * @param content 检索内容
     * @return List<House>
     */
    List<House> querySorted(Es es, String field, String content);
}
