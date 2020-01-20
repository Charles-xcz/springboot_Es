package com.ustc.charles;

import com.ustc.charles.config.ElasticSearchConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author charles
 */
@SpringBootApplication
public class EsApplication {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
        logger.info(" application running...");
    }
}
