package io.medalytics.microservice.demo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {
    private String keySerializerClass;
    private String valueSerializerClass;
    private String acks;
    private String compressionType;
    private Integer retryCount;
    private Integer batchSize;
    private Integer batchSizeBoostFactor;
    private Integer requestTimeoutMs;
    private Integer lingerMs;
}
