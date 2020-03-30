package com.ustc.charles.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author charles
 * @date 2020/3/27 11:48
 */
@Data
@Accessors(chain = true)
public class HouseTag {
    private Long id;
    private Long houseId;
    private String name;
}
