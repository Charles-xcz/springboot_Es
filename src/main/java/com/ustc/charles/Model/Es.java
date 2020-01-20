package com.ustc.charles.Model;

import lombok.Data;


/**
 * @author charles
 */
@Data
public class Es {

    private String index;

    private String type;

    public Es(String index, String type) {
        this.index = index;
        this.type = type;
    }
}
