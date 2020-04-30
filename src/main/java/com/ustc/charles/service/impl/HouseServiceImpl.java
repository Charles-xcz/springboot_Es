package com.ustc.charles.service.impl;

import com.ustc.charles.dao.mapper.HouseMapper;
import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.entity.HouseForm;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.event.EventMessage;
import com.ustc.charles.event.EventProducer;
import com.ustc.charles.model.House;
import com.ustc.charles.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/27 11:03
 */
@Slf4j
@Service
public class HouseServiceImpl implements HouseService {
    @Resource
    private HouseMapper houseMapper;
    @Autowired
    private EventProducer eventProducer;

    @Transactional
    @Override
    public ServiceResult<House> save(HouseForm houseForm) {
        House house = new House();
        BeanUtils.copyProperties(houseForm, house);
        house.setCreateTime(new Date());
        house.setUpdateTime(new Date());
        houseMapper.save(house);

        EventMessage<House> message = new EventMessage<House>().setTopic(EventMessage.TOPIC_PUBLISH).setData(house);
        log.debug("发布 house_publish 消息:{}", message);
        eventProducer.fireEvent(message);
        return new ServiceResult<>(true, null, house);
    }

    @Override
    public ServiceMultiResult<House> adminQuery(DatatableSearch searchBody) {
        if ("createTime".equals(searchBody.getOrderBy())) {
            searchBody.setOrderBy("create_time");
        } else if ("totalPrice".equals(searchBody.getOrderBy())) {
            searchBody.setOrderBy("total_Price");
        }
        List<House> houses = houseMapper.findHouses(searchBody);
        int total = houseMapper.findHousesCount(searchBody);
        return new ServiceMultiResult<>(houses, total);
    }

    @Transactional
    @Override
    public ServiceResult update(HouseForm houseForm) {
        House house = houseMapper.selectHouseById(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }
        BeanUtils.copyProperties(houseForm, house);
        house.setUpdateTime(new Date());
        houseMapper.update(house);
        EventMessage<House> message = new EventMessage<House>().setTopic(EventMessage.TOPIC_UPDATE).setData(house);
        log.debug("发布 house_update 消息:{}", message);
        eventProducer.fireEvent(message);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteHouse(Long id) {
        houseMapper.delete(id);
        EventMessage message = new EventMessage<>().setTopic(EventMessage.TOPIC_REMOVE).setHouseId(id);
        eventProducer.fireEvent(message);
        return ServiceResult.success();
    }


    @Override
    public ServiceResult<House> findById(Long houseId) {
        House house = houseMapper.selectHouseById(houseId);
        if (house != null) {
            return ServiceResult.of(house);
        }
        return ServiceResult.notFound();
    }


}
