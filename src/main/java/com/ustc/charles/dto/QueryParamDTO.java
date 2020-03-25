package com.ustc.charles.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 筛选条件封装成对象
 *
 * @author charles
 * @date 2020/2/17 16:21
 */
@Data
public class QueryParamDTO implements Serializable {
    private String keyword;
    private List<String> locals;
    private List<String> price;
    private List<String> houseType;
    private Double[] area;
    private List<String> layout;
    private List<String> floor;
    private List<String> design;
    private List<String> decorate;
    private List<String> lift;
    private List<String> liftProportion;
    private List<String> region;
    private List<String> community;
}
