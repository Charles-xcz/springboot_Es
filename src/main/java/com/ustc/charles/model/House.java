package com.ustc.charles.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author charles
 * @date 2020/1/20 16:38
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class House {
    private String id;
    private String name;
    private String title;
    @JSONField(name = "house_type")
    private String houseType;
    private String position;
    private String longitude;
    private String latitude;
    private String layout;
    private String floor;
    private Double area;
    private String design;
    private String decorate;
    private String lift;
    @JSONField(name = "lift_proportion")
    private String liftProportion;
    @JSONField(name = "total_price")
    private Double totalPrice;
    @JSONField(name = "avg_price")
    private Double avgPrice;
    private String region;
    private String local;
    private String community;
    @JSONField(name = "create_time")
    private Date createTime;
    @JSONField(name = "update_time")
    private Date updateTime;

}
