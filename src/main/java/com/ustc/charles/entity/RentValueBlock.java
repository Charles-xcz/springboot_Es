package com.ustc.charles.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/28 21:13
 */
public class RentValueBlock {
    /**
     * 价格区间定义
     */
    public static final Map<String, RentValueBlock> PRICE_BLOCK;

    /**
     * 面积区间定义
     */
    public static final Map<String, RentValueBlock> AREA_BLOCK;

    /**
     * 无限制区间
     */
    public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

    static {
        PRICE_BLOCK = new HashMap<>();
        PRICE_BLOCK.put("*-1000", new RentValueBlock("*-1000", -1, 1000));
        PRICE_BLOCK.put("1000-3000", new RentValueBlock("1000-3000", 1000, 3000));
        PRICE_BLOCK.put("3000-*", new RentValueBlock("3000-*", 3000, -1));
        AREA_BLOCK = new HashMap<>();
        AREA_BLOCK.put("*-30", new RentValueBlock("*-30", -1, 30));
        AREA_BLOCK.put("30-50", new RentValueBlock("30-50", 30, 50));
        AREA_BLOCK.put("50-*", new RentValueBlock("50-*", 50, -1));

    }

    private String key;
    private int min;
    private int max;

    public RentValueBlock(String key, int min, int max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public static RentValueBlock matchPrice(String key) {
        RentValueBlock block = PRICE_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }

    public static RentValueBlock matchArea(String key) {
        RentValueBlock block = AREA_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }
}
