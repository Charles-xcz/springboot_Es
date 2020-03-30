package com.ustc.charles.event;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author charles
 * @date 2020/3/29 22:28
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     *
     * @param message 事件
     */
    public void fireEvent(EventMessage message) {
        //将事件发布到指定的主题
        kafkaTemplate.send(message.getTopic(), JSONObject.toJSONString(message));
    }
}
