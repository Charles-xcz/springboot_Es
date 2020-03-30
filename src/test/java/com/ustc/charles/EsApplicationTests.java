package com.ustc.charles;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EsApplicationTests {


    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    void contextLoads() {
    }


}
