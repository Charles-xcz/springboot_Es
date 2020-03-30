package com.ustc.charles.service;

import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.Subway;
import com.ustc.charles.model.SubwayStation;
import com.ustc.charles.model.SupportAddress;

import java.util.Map;

/**
 * @author charles
 * @date 2020/3/27 10:45
 */
public interface AddressService {

    /**
     * 获取所有支持的城市列表
     *
     * @return
     */
    ServiceMultiResult<SupportAddress> findAllCities();

    /**
     * 根据英文简写获取具体区域的信息
     *
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    Map<SupportAddress.Level, SupportAddress> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     *
     * @param cityName
     * @return
     */
    ServiceMultiResult<SupportAddress> findAllRegionsByCityName(String cityName);

//        /**
//         * 获取该城市所有的地铁线路
//         *
//         * @param cityEnName
//         * @return
//         */
//        List<SubwayDTO> findAllSubwayByCity(String cityEnName);
//
//        /**
//         * 获取地铁线路所有的站点
//         *
//         * @param subwayId
//         * @return
//         */
//        List<SubwayStationDTO> findAllStationBySubway(Long subwayId);
//
//        /**
//         * 获取地铁线信息
//         *
//         * @param subwayId
//         * @return
//         */
//        ServiceMultiResult<SubwayDTO> findSubway(Long subwayId);
//
//        /**
//         * 获取地铁站点信息
//         *
//         * @param stationId
//         * @return
//         */
//        ServiceMultiResult<SubwayStationDTO> findSubwayStation(Long stationId);

    /**
     * 根据城市英文简写获取城市详细信息
     *
     * @param cityEnName
     * @return
     */
    ServiceResult<SupportAddress> findCity(String cityEnName);

    ServiceResult<Subway> findSubway(Long subwayLineId);

    ServiceResult<SubwayStation> findSubwayStation(Long subwayStationId);
//
//        /**
//         * 根据城市以及具体地位获取百度地图的经纬度
//         */
//        ServiceMultiResult<BaiduMapLocation> getBaiduMapLocation(String city, String address);
//
//        /**
//         * 上传百度LBS数据
//         */
//        ServiceMultiResult lbsUpload(BaiduMapLocation location, String title, String address,
//                                     long houseId, int price, int area);
//    /**
//     * 移除百度LBS数据
//     *
//     * @param houseId
//     * @return
//     */
//    ServiceMultiResult removeLbs(Long houseId);
}
