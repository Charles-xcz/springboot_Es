package com.ustc.charles.service.impl;

import com.ustc.charles.dao.mapper.SupportAddressMapper;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.Subway;
import com.ustc.charles.model.SubwayStation;
import com.ustc.charles.model.SupportAddress;
import com.ustc.charles.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/27 10:50
 */
@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    @Resource
    private SupportAddressMapper supportAddressMapper;

    @Override
    public ServiceMultiResult<SupportAddress> findAllCities() {
        List<SupportAddress> addresses = supportAddressMapper.findAllByLevel(SupportAddress.Level.CITY.getValue());
        return new ServiceMultiResult<>(addresses, addresses.size());
    }

    @Override
    public Map<SupportAddress.Level, SupportAddress> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddress> result = new HashMap<>();

        log.debug("findCityAndRegion({},{})", cityEnName, regionEnName);
        if (StringUtils.isAnyBlank(cityEnName, regionEnName)) {
//            throw new IllegalArgumentException("参数不能为空!");
            result.put(SupportAddress.Level.CITY, new SupportAddress().setEnName("bj").setCnName("北京"));
            result.put(SupportAddress.Level.REGION, new SupportAddress().setEnName("xcq").setCnName("西城区"));
            return result;
        }

        SupportAddress city = supportAddressMapper.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        SupportAddress region = supportAddressMapper.findByEnNameAndBelongTo(regionEnName, city.getEnName());
        result.put(SupportAddress.Level.CITY, city);
        result.put(SupportAddress.Level.REGION, region);
        return result;
    }

    @Override
    public ServiceMultiResult<SupportAddress> findAllRegionsByCityName(String cityName) {
        if (cityName == null) {
            return new ServiceMultiResult<>(null, 0);
        }
        List<SupportAddress> regions = supportAddressMapper.findAllByLevelAndBelongTo(SupportAddress.Level.REGION
                .getValue(), cityName);
        return new ServiceMultiResult<>(regions, regions.size());
    }

    @Override
    public ServiceResult<SupportAddress> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }
        SupportAddress supportAddress = supportAddressMapper.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (supportAddress == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(supportAddress);
    }

    @Override
    public ServiceResult<Subway> findSubway(Long subwayLineId) {
        return null;
    }

    @Override
    public ServiceResult<SubwayStation> findSubwayStation(Long subwayStationId) {
        return null;
    }
}
