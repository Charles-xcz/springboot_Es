package com.ustc.charles.dao;

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
     * @param es     es
     * @param type   houseType
     * @param layout layout
     * @param region region
     * @return List<House>
     */
    List<House> queryShould(Es es, String type, String layout, String region);

    /**
     * 查询所有房屋信息
     *
     * @param es -> index,type
     * @return List<House>
     */
    List<House> queryAll(Es es);

    /**
     * 分页查询房屋信息
     *
     * @param es          index,type
     * @param currentPage 当前页
     * @param pageSize    每页信息条数
     * @return List<House>
     */
    List<House> queryBySplitPage(Es es, Integer currentPage, Integer pageSize);

    /**
     * 排序查询,依据得分降序排列
     *
     * @param es        index,type
     * @param field 检索字段
     * @param content   检索内容
     * @return List<House>
     */
    List<House> querySorted(Es es, String field, String content);
}
