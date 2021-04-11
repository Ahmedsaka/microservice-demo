package io.medalytics.microservice.demo.twitter.to.kafka;

import lombok.Data;
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
