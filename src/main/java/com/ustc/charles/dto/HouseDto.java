package com.ustc.charles.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author charles
 * @date 2020/3/27 10:57
 */
@Data
public class HouseDto implements Serializable {
    private static final long serialVersionUID = 8918735582286008182L;
    private Long id;
    private String title;
    private String url;
    private String houseType;
    private String positions;
    private String longitude;
    private String latitude;
    private String layout;
    private String floor;
    private Double area;
    private String design;
    private String direction;
    private String decorate;
    private String lift;
    private String liftProportion;
    @JsonProperty("total_price")
    private Double totalPrice;
    @JsonProperty("total_price")
    private Double avgPrice;
    private String region;
    private String locals;
    private String community;
    private Date createTime;
    private Date updateTime;
    private String status;
}
