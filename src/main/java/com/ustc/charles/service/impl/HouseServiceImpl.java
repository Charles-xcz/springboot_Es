package com.ustc.charles.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.ustc.charles.dao.mapper.HouseMapper;
import com.ustc.charles.dao.mapper.HousePictureMapper;
import com.ustc.charles.dao.mapper.HouseTagMapper;
import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.HouseDto;
import com.ustc.charles.entity.*;
import com.ustc.charles.event.EventMessage;
import com.ustc.charles.event.EventProducer;
import com.ustc.charles.model.House;
import com.ustc.charles.model.HousePicture;
import com.ustc.charles.model.HouseTag;
import com.ustc.charles.model.User;
import com.ustc.charles.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private HostHolder hostHolder;
    @Resource
    private HousePictureMapper housePictureMapper;
    @Resource
    private HouseTagMapper houseTagMapper;
    @Value("${qiniu.bucket.url}")
    private String bucketUrl;
    @Autowired
    private QiNiuServiceImpl qiNiuService;
    @Autowired
    private EventProducer eventProducer;

    @Transactional
    @Override
    public ServiceResult<HouseDto> save(HouseForm houseForm) {
        House house = new House();
        BeanUtils.copyProperties(houseForm, house);
        house.setCreateTime(new Date());
        house.setUpdateTime(new Date());
        User user = hostHolder.getUser();
        if (user != null) {
            house.setAdminId(user.getId());
        }
        houseMapper.save(house);
        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        housePictureMapper.save(pictures);
        HouseDto houseDto = new HouseDto();
        BeanUtils.copyProperties(house, houseDto);

        houseDto.setPictures(pictures);
        houseDto.setCover(this.bucketUrl + houseDto.getCover());
        List<String> tags = houseForm.getTags();
        if (tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag().setHouseId(house.getId()).setName(tag));
            }
            houseTagMapper.save(houseTags);
            houseDto.setTags(tags);
        }
        EventMessage<House> message = new EventMessage<House>().setTopic(EventMessage.TOPIC_PUBLISH).setData(house);
        log.debug("发布 house_publish 消息:{}", message);
        eventProducer.fireEvent(message);
        return new ServiceResult<>(true, null, houseDto);
    }

    @Override
    public ServiceMultiResult<HouseDto> adminQuery(DatatableSearch searchBody) {
        List<HouseDto> houseDTOS = new ArrayList<>();
        if ("createTime".equals(searchBody.getOrderBy())) {
            searchBody.setOrderBy("create_time");
        } else if ("watchTimes".equals(searchBody.getOrderBy())) {
            searchBody.setOrderBy("watch_times");
        }
        List<House> houses = houseMapper.findHouses(searchBody);
        int total = houseMapper.findHousesCount(searchBody);
        houses.forEach(house -> {
            HouseDto houseDto = new HouseDto();
            BeanUtils.copyProperties(house, houseDto);
            houseDto.setCover(this.bucketUrl + house.getCover());
            houseDTOS.add(houseDto);
        });
        return new ServiceMultiResult<>(houseDTOS, total);
    }

    @Override
    public ServiceResult<HouseDto> findCompleteById(Long id) {
        House house = houseMapper.selectHouseById(id);
        if (house == null) {
            return ServiceResult.notFound();
        }
        List<HousePicture> pictures = housePictureMapper.selectPicturesByHouseId(id);
        List<HouseTag> tags = houseTagMapper.selectTagsByHouseId(id);
        List<String> tagList = new ArrayList<>();
        for (HouseTag tag : tags) {
            tagList.add(tag.getName());
        }
        HouseDto result = new HouseDto();
        BeanUtils.copyProperties(house, result);
        result.setPictures(pictures);
        result.setTags(tagList);
        return ServiceResult.of(result);
    }

    @Transactional
    @Override
    public ServiceResult update(HouseForm houseForm) {
        House house = houseMapper.selectHouseById(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }
        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        if (pictures.size() > 0) {
            List<Long> ids = new ArrayList<>();
            pictures.forEach(picture -> ids.add(picture.getId()));
            housePictureMapper.delete(ids);
            housePictureMapper.save(pictures);
        }
        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }
        BeanUtils.copyProperties(houseForm, house);
        house.setUpdateTime(new Date());
        houseMapper.update(house);
        EventMessage<House> message = new EventMessage<House>().setTopic(EventMessage.TOPIC_UPDATE).setData(house);
        log.debug("发布 house_update 消息:{}", message);
        eventProducer.fireEvent(message);
        return ServiceResult.success();
    }

    @Transactional
    @Override
    public ServiceResult updateCover(Long coverId, Long houseId) {
        HousePicture cover = housePictureMapper.selectById(coverId);
        if (cover == null) {
            return ServiceResult.notFound();
        }
        houseMapper.updateCover(houseId, cover.getPath());
        EventMessage<House> message = new EventMessage<House>().setTopic(EventMessage.TOPIC_UPDATE).setHouseId(houseId);
        log.debug("发布 house_update 消息:{}", message);
        eventProducer.fireEvent(message);
        return ServiceResult.success();
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseMapper.selectHouseById(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagMapper.findByNameAndHouseId(tag, houseId);
        if (houseTag != null) {
            return new ServiceResult(false, "标签已存在");
        }
        houseTagMapper.save(new HouseTag().setHouseId(houseId).setName(tag));
        return ServiceResult.success();
    }

    @Transactional
    @Override
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseMapper.selectHouseById(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagMapper.findByNameAndHouseId(tag, houseId);
        if (houseTag == null) {
            return new ServiceResult(false, "标签不存在");
        }
        houseTagMapper.deleteTag(houseTag.getId());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureMapper.selectById(id);
        if (picture == null) {
            return ServiceResult.notFound();
        }
        try {
            Response response = qiNiuService.delete(picture.getPath());
            if (response.isOK()) {
                housePictureMapper.deleteById(id);
                return ServiceResult.success();
            } else {
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }



    @Override
    public ServiceResult<House> findById(Long houseId) {
        House house = houseMapper.selectHouseById(houseId);
        if (house != null) {
            return ServiceResult.of(house);
        }
        return ServiceResult.notFound();
    }

    /**
     * 图片对象列表信息填充
     *
     * @param form
     * @param houseId
     * @return
     */
    private List<HousePicture> generatePictures(HouseForm form, Long houseId) {
        List<HousePicture> pictures = new ArrayList<>();

        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photoForm : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setBucketUrl(bucketUrl);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }

}
