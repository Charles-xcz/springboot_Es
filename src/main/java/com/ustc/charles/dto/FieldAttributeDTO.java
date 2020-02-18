package com.ustc.charles.dto;

import lombok.Data;

import java.util.List;

/**
 * 字段属性
 *
 * @author charles
 * @date 2020/2/18 16:27
 */
@Data
public class FieldAttributeDTO {
    private String field;
    private String name;
    private List<String> values;
}
