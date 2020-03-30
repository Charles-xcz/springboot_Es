package com.ustc.charles.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author charles
 * @date 2020/3/27 20:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiDataTableResponse extends ApiResponse {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;

    public ApiDataTableResponse(ApiResponse.Status status) {
        this(status.getCode(), status.getStandardMessage(), null);
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }
}

