package com.ustc.charles.dao;

import com.ustc.charles.model.House;

/**
 * @author charles
 * @date 2020/1/21 10:39
 */
@Deprecated
public interface HouseDao {
    /**
     * 增加房屋
     *
     * @param house ->document
     * @return result
     */
    String add(House house);

    /**
     * 根据id删除房屋信息
     *
     * @param id 房屋id
     * @return result
     */
    String deleteById(String id);

    /**
     * 更新房屋信息
     *
     * @param house 房屋
     * @return result
     */
    String update(House house);

    /**
     * 获取房屋数量
     *
     * @return 数量
     */
    Long getCount();

}
