package com.ustc.charles.dao.mapper;

import com.ustc.charles.model.HouseDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author charles
 * @date 2020/3/27 12:13
 */
@Mapper
public interface HouseDetailMapper {
    int save(HouseDetail houseDetail);

    HouseDetail selectByHouseId(Long houseId);

    int update(HouseDetail detail);
}
