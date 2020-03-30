package com.ustc.charles.config;


import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author charles
 */
//@Configuration
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

    /**
     * ES TransportClient 配置
     */
//    @Bean
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

    /**
     * 批操作配置
     */
//    @Bean
    public BulkProcessor bulkProcessor() throws UnknownHostException {
        Settings settings = Settings.EMPTY;
        if (clusterName != null && clusterName.length() != 0) {
            settings = Settings.builder()
                    .put("cluster.name", clusterName)
                    .build();
        }
        TransportClient transportClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        return BulkProcessor.builder(transportClient, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {

            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                logger.error("{} data bulk failed,reason :{}", bulkRequest.numberOfActions(), throwable);
            }
        })
                //分批，每10000条请求为一批
                .setBulkActions(1000)
                //每次5MB，刷新一次bulk。默认为5m
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                //每5秒一定执行，不管已经队列积累了多少。默认不设置这个值
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                //设置并发请求数，如果是0，那表示只有一个请求就可以被执行，如果为1，则可以积累并被执行。默认为1.
                .setConcurrentRequests(1)
                /*
                   这里有个backoff策略，最初等待100ms,然后按照指数增长并重试3次。
                   每当一个或者多个bulk请求失败,并出现EsRejectedExecutionException异常时.就会尝试重试。
                   这个异常表示用于处理请求的可用计算资源太少。
                   如果要禁用这个backoff策略，需要用backoff.nobackoff()。
                 */
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
    }
}
