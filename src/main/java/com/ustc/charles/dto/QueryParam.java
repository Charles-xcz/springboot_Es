package com.ustc.charles.dto;

import lombok.Data;

/**
 * @author charles
 * @date 2020/2/17 16:21
 */
@Data
public class QueryParam {
    private String keyword;
    private String[] position;
    private String[] price;
    private String[] houseType;
    private Double[] area;
    private String[] layout;
    private String[] floor;
    private String[] design;
    private String[] decorate;
    private String[] lift;
    private String[] liftProportion;
    private String[] region;
    private String[] community;
}
