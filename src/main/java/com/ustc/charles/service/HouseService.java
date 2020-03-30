package com.ustc.charles.service;

import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.HouseDto;
import com.ustc.charles.entity.HouseForm;
import com.ustc.charles.entity.RentSearch;
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
     *
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDto> save(HouseForm houseForm);

    ServiceMultiResult<HouseDto> adminQuery(DatatableSearch searchBody);

    ServiceResult<HouseDto> findCompleteById(Long houseId);

    ServiceResult update(HouseForm houseForm);

    ServiceResult updateCover(Long coverId, Long targetId);

    ServiceResult addTag(Long houseId, String tag);

    ServiceResult removeTag(Long houseId, String tag);

    ServiceResult removePhoto(Long id);

    ServiceMultiResult<HouseDto> query(RentSearch rentSearch);

    ServiceResult<House> findById(Long houseId);
}
