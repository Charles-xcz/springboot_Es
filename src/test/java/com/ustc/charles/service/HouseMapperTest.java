package com.ustc.charles.service;

import com.ustc.charles.EsApplicationTests;
import com.ustc.charles.dao.mapper.HouseMapper;
import com.ustc.charles.dao.mapper.SupportAddressMapper;
import com.ustc.charles.model.House;
import com.ustc.charles.model.SupportAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 12:03
 */
public class HouseMapperTest extends EsApplicationTests {
    @Resource
    HouseMapper houseMapper;

    @Test
    public void testSave() {
        House house = new House();
        house.setTitle("kdjdjfflsaj");
        house.setCreateTime(new Date());
        house.setUpdateTime(new Date());
        houseMapper.save(house);
        System.out.println(house);
    }

    @Test
    public void testSelect() {
        List<House> houses = houseMapper.selectHouses(0, 10);
        houses.forEach(System.out::println);
    }

    @Resource
    SupportAddressMapper supportAddressMapper;

    @Test
    public void testFindAllCities() {
        List<SupportAddress> allByLevel = supportAddressMapper.findAllByLevel(SupportAddress.Level.CITY.getValue());
        allByLevel.forEach(System.out::println);
    }
}
