package com.ustc.charles.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 复合查询条件封装成对象
 *
 * @author charles
 * @date 2020/2/17 16:21
 */
@Data
public class QueryParamDto implements Serializable {
    private String cityName = "苏州";
    private String keyword;
    private List<String> region;
    private List<String> locals;
    private List<String> community;
    private List<String> price;
    private List<String> houseType;
    private List<String> area;
    private List<String> layout;
    private List<String> floor;
    private List<String> design;
    private List<String> decorate;
    private List<String> lift;
    private List<String> liftProportion;
}
