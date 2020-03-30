package com.ustc.charles.dao.mapper;

import com.ustc.charles.model.HouseTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 12:34
 */
@Mapper
public interface HouseTagMapper {
    List<HouseTag> save(List<HouseTag> houseTags);

    HouseTag save(HouseTag houseTag);

    List<HouseTag> selectTagsByHouseId(Long houseId);

    HouseTag findByNameAndHouseId(String tagName, Long houseId);

    int deleteTag(Long id);
}
