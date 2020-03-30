package com.ustc.charles.event;

import com.ustc.charles.model.House;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.annotation.Target;

/**
 * @author charles
 * @date 2020/3/29 22:44
 */
@Data
@Accessors(chain = true)
public class EventMessage<T> {
    public static final int MAX_RETRY = 3;
    public static final String TOPIC_PUBLISH = "house_publish";
    public static final String TOPIC_UPDATE = "house_update";
    public static final String TOPIC_REMOVE = "house_REMOVE";

    private int retry = 0;
    private String topic;
    private Long houseId;
    private T data;
}
