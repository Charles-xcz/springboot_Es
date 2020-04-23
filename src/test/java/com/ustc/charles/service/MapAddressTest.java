package com.ustc.charles.service;

import com.ustc.charles.EsApplicationTests;
import com.ustc.charles.dto.MapAddressDto;
import com.ustc.charles.entity.ServiceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author charles
 * @date 2020/4/4 21:37
 */
public class MapAddressTest extends EsApplicationTests {
    @Autowired
    private AddressService addressService;

    @Test
    public void testPositionToAddress() {
        ServiceResult<MapAddressDto> result = addressService.positionToAddress("31.335843", "120.737694");
        if (result.isSuccess()) {
            System.out.println(result.getResult());
        }
    }
}
