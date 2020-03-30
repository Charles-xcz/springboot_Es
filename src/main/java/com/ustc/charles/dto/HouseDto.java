package com.ustc.charles.dto;

import com.ustc.charles.model.HouseDetail;
import com.ustc.charles.model.HousePicture;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 10:57
 */
@Data
public class HouseDto implements Serializable {
    private static final long serialVersionUID = 8918735582286008182L;
    private Long id;
    private String title;
    private int price;
    private int area;
    private int direction;
    private int room;
    private int parlour;
    private int bathroom;
    private int floor;
    private Long adminId;
    private String district;
    private int totalFloor;
    private int watchTimes;
    private int buildYear;
    private int status;
    private Date createTime;
    private Date lastUpdateTime;
    private String cityEnName;
    private String regionEnName;
    private String street;
    private String cover;
    private int distanceToSubway;
    private HouseDetail houseDetail;
    private List<String> tags;
    private List<HousePicture> pictures;
    private int subscribeStatus;
}
