package com.ustc.charles.model;

import lombok.Data;

/**
 * @author charles
 * @date 2020/3/27 12:16
 */
@Data
public class HousePicture {
    private Long id;

    private Long houseId;

    private String path;

    private String bucketUrl;

    private int width;

    private int height;

    private String location;
}
