package com.ustc.charles.event;

import com.alibaba.fastjson.JSONObject;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.service.HouseService;
import com.ustc.charles.service.impl.EsHouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author charles
 * @date 2020/3/29 22:28
 */
@Component
@Slf4j
public class EventConsumer {
    @Autowired
    private HouseService houseService;
    @Autowired
    private EsHouseServiceImpl esHouseService;

    @KafkaListener(topics = {EventMessage.TOPIC_PUBLISH, EventMessage.TOPIC_UPDATE, EventMessage.TOPIC_REMOVE})
    public void handlerAddMessage(ConsumerRecord record) {
        if (record == null) {
            log.error("消息内容为空");
            return;
        }
        EventMessage<House> message = JSONObject.parseObject(record.value().toString(), EventMessage.class);
        if (message == null) {
            log.error("消息格式错误");
            return;
        }
        switch (message.getTopic()) {
            case EventMessage.TOPIC_PUBLISH:
                House house = message.getData();
                esHouseService.save(house);
                break;
            case EventMessage.TOPIC_UPDATE:
                ServiceResult<House> result = houseService.findById(message.getHouseId());
                if (result.isSuccess()) {
                    esHouseService.save(result.getResult());
                }
                break;
            case EventMessage.TOPIC_REMOVE:
                esHouseService.deleteById(message.getHouseId());
                break;
            default:
                log.warn("no support message:{}", message);
                break;
        }
    }
}
