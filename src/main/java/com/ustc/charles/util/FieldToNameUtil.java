package com.ustc.charles.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author charles
 * @date 2020/2/18 20:34
 */
public class FieldToNameUtil {
    public static Map<String, String> fieldToName = new HashMap<>();

    static {
        fieldToName.put("houseType", "类型");
        fieldToName.put("layout", "布局");
        fieldToName.put("floor", "楼层");
        fieldToName.put("design", "设计");
        fieldToName.put("decorate", "装修");
        fieldToName.put("liftProportion", "电梯");
        fieldToName.put("region", "地区");
        fieldToName.put("community", "社区");
    }
}
