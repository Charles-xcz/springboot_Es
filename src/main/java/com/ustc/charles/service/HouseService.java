package com.ustc.charles.service;

import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.entity.HouseForm;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;

/**
 * @author charles
 * @date 2020/1/21 12:10
 */
public interface HouseService {
    /**
     * 新增
     */
    ServiceResult<House> save(HouseForm houseForm);


    ServiceResult update(HouseForm houseForm);

    ServiceResult deleteHouse(Long id);


    ServiceResult<House> findById(Long houseId);

}
