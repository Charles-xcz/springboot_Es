package com.ustc.charles.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 9:52
 */
@Data
@AllArgsConstructor
public class ServiceMultiResult<T> {
    List<T> result;
    long total;
    public int getResultSize() {
        if (this.result == null) {
            return 0;
        }
        return this.result.size();
    }
}
