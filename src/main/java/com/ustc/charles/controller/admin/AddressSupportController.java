package com.ustc.charles.controller.admin;

import com.alibaba.fastjson.JSON;
import com.ustc.charles.dto.ApiResponse;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.model.SupportAddress;
import com.ustc.charles.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author charles
 * @date 2020/3/27 16:59
 */
@Controller
@RequestMapping("/address/support")
@Slf4j
public class AddressSupportController {
    @Autowired
    private AddressService addressService;

    /**
     * 获取支持城市列表
     *
     * @return
     */
    @GetMapping("/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddress> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        log.debug("城市:{}", JSON.toJSONString(result.getResult()));

        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取对应城市支持区域列表
     *
     * @param cityEnName
     * @return
     */
    @GetMapping("/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {
        ServiceMultiResult<SupportAddress> addressResult = addressService.findAllRegionsByCityName(cityEnName);
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(addressResult.getResult());
    }
}
