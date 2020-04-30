package com.ustc.charles.dao.mapper;

import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.model.House;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * House的数据库操作
 *
 * @author charles
 * @date 2020/3/25 7:45
 */
@Mapper
public interface HouseMapper {
    List<House> selectHouses();

    List<House> selectHouses(int offset, int limit);

    int save(House house);

    int update(House house);

    List<House> findHouses(DatatableSearch searchBody);

    int findHousesCount(DatatableSearch searchBody);

    House selectHouseById(Long id);
    List<House> selectHousesByIds(List<Long> ids);

    int updateCover(Long houseId, String path);

    long delete(Long id);
}
