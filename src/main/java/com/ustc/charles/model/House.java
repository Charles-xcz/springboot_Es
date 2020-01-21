package com.ustc.charles.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

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
    private String area;
    private String design;
    private String decorate;
    private String lift;
    @JSONField(name = "lift_proportion")
    private String liftProportion;
    @JSONField(name = "total_price")
    private Integer totalPrice;
    @JSONField(name = "avg_price")
    private Integer avgPrice;
    private String region;
    private String local;
    private String community;

}
