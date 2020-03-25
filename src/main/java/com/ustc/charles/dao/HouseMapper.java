package com.ustc.charles.dao;

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
}
