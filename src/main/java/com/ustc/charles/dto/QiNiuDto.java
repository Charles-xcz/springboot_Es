package com.ustc.charles.dto;

import lombok.Data;

/**
 * @author charles
 * @date 2020/3/26 22:27
 */
@Data
public final class QiNiuDto {
    public String key;
    public String hash;
    public String bucket;
    public int width;
    public int height;
}
