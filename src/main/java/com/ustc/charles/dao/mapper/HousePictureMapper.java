package com.ustc.charles.dao.mapper;

import com.ustc.charles.model.HousePicture;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 12:15
 */
@Mapper
public interface HousePictureMapper {
    int save(List<HousePicture> pictures);

    int deleteByHouseId(Long houseId);

    List<HousePicture> selectPicturesByHouseId(Long houseId);

    HousePicture selectById(Long id);

    int deleteById(Long id);

    int delete(List<Long> ids);
}
