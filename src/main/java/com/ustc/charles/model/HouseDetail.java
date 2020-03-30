package com.ustc.charles.model;

import lombok.Data;

/**
 * @author charles
 * @date 2020/3/27 11:44
 */
@Data
public class HouseDetail {
    private Long id;
    private Long houseId;
    private String description;
    private String layoutDesc;
    private String traffic;
    private String roundService;
    private String address;
    private Long subwayLineId;
    private Long subwayStationId;
    private String subwayLineName;
    private String subwayStationName;
}
