package com.ustc.charles.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段属性
 *
 * @author charles
 * @date 2020/2/18 16:27
 */
@Data
public class FieldAttributeDto {
    public static final Map<String, String> FIELD_TO_NAME = new HashMap<>();

    static {
        FIELD_TO_NAME.put("region", "区域");
        FIELD_TO_NAME.put("locals", "地区");
        FIELD_TO_NAME.put("community", "小区");
        FIELD_TO_NAME.put("houseType", "类型");
        FIELD_TO_NAME.put("layout", "布局");
        FIELD_TO_NAME.put("floor", "楼层");
        FIELD_TO_NAME.put("design", "设计");
        FIELD_TO_NAME.put("decorate", "装修");
        FIELD_TO_NAME.put("liftProportion", "电梯");
    }

    /**
     * 字段
     */
    private String field;
    /**
     * 字段的中文名
     */
    private String name;
    /**
     * 字段值的聚合
     */
    private List<HouseBucketDto> houseBuckets;
}
