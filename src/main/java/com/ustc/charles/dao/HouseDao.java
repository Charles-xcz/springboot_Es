package com.ustc.charles.dao;

import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;

import java.util.List;

/**
 * @author charles
 * @date 2020/1/21 10:39
 */
public interface HouseDao {
    /**
     * 增加房屋
     *
     * @param es    -> index,type
     * @param house ->document
     * @return result
     */
    String add(Es es, House house);

    /**
     * 根据id删除房屋信息
     *
     * @param es -> index,type
     * @param id 房屋id
     * @return result
     */
    String deleteById(Es es, String id);

    /**
     * 更新房屋信息
     *
     * @param es    -> index,type
     * @param house 房屋
     * @return result
     */
    String update(Es es, House house);

    /**
     * 获取房屋数量
     *
     * @param es -> index,type
     * @return 数量
     */
    Long getCount(Es es);

    /**
     * 获取字段属性
     * @param es es
     * @return List<FieldAttribute>
     */
    List<FieldAttributeDTO> getFieldAttribute(Es es);
}
