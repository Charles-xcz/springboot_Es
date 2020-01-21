package com.ustc.charles.config;


import com.ustc.charles.model.Es;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author charles
 */
@Configuration
public class ElasticSearchConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private Integer port;
    @Value("${elasticsearch.cluster-name}")
    private String clusterName;
    @Value("${elasticsearch.index}")
    private String index;
    @Value("${elasticsearch.type}")
    private String type;
    private TransportClient transportClient;

    @Bean
    public Es es() {
        return new Es(index, type);
    }

    @Bean
    public TransportClient transportClient() {
        Settings settings = Settings.EMPTY;
        if (clusterName != null && clusterName.length() != 0) {
            settings = Settings.builder()
                    .put("cluster.name", clusterName)
                    .build();
        }
        try {
            transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            logger.error("创建elasticsearch客户端失败");
            e.printStackTrace();
        }
        logger.info("创建elasticsearch客户端成功");
        return transportClient;
    }
}
