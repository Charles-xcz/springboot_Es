package com.ustc.charles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author charles
 * @date 2020/4/4 20:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseBucketDto {
    /**
     * 聚合bucket的key
     */
    private String key;
    /**
     * 聚合结果值
     */
    private long count;

}
