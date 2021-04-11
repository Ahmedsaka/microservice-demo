package io.medalytics.microservice.demo.config;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
    private List<String> twitterKeywords;
    private Boolean enableMockTweets;
    private int mockMinTweetLength;
    private int mockMaxTweetLength;
    private Long mockSleepMs;
}
