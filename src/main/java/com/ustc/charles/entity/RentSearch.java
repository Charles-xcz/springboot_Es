package com.ustc.charles.entity;

import lombok.Data;

/**
 * @author charles
 * @date 2020/3/28 21:02
 */
@Data
public class RentSearch {
    private String cityEnName;
    private String regionEnName;
    private String priceBlock;
    private String areaBlock;
    private int room;
    private int direction;
    private String keywords;
    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";
    private String rentWay;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int start = 0;

    private int size = 5;

    public int getStart() {
        return Math.max(start, 0);
    }

    public int getSize() {
        if (this.size < 1) {
            return 5;
        } else {
            return Math.min(this.size, 100);
        }
    }

}
