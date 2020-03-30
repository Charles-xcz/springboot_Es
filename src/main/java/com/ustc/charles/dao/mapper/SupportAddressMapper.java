package com.ustc.charles.dao.mapper;

import com.ustc.charles.model.SupportAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 17:11
 */
@Mapper
public interface SupportAddressMapper {
    List<SupportAddress> findAllByLevel(String level);

    SupportAddress findByEnNameAndLevel(String cityEnName, String level);

    SupportAddress findByEnNameAndBelongTo(String regionEnName, String cityName);

    List<SupportAddress> findAllByLevelAndBelongTo(String level, String cityName);
}
