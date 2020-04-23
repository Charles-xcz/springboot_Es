package com.ustc.charles.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.ustc.charles.EsApplicationTests;
import com.ustc.charles.service.impl.QiNiuServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author charles
 * @date 2020/3/26 22:29
 */
public class QiNiuServiceTest extends EsApplicationTests {
    @Autowired
    private QiNiuServiceImpl qiNiuService;

    @Test
    public void testUploadFile() {
        String fileName = "D:\\charles\\Pictures\\litter\\5ceb0e8ca9773912d467c113f2198618377ae26d.jpg";
        File file = new File(fileName);
        Assertions.assertTrue(file.exists());
        try {
            Response response = qiNiuService.uploadFile(file);
            Assertions.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        String key = "FuU7RG5wMuulih9wB1fK9QG0QXoC";
        try {
            Response response = qiNiuService.delete(key);
            Assertions.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

}
