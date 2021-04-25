package io.medalytics.microservice.demo.kafka.admin.client;

import io.medalytics.microservice.demo.kafka.admin.exceptions.KafkaClientException;
import io.medalytics.microservice.demo.common.config.KafkaConfigData;
import io.medalytics.microservice.demo.common.config.RetryConfigData;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData,
                            RetryConfigData retryConfigData,
                            AdminClient adminClient,
                            RetryTemplate retryTemplate,
                            WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() throws KafkaClientException {
        CreateTopicsResult createTopicsResult;
        try {
             createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        } catch (Throwable T) {
            throw new KafkaClientException("Reached max number of retry for creating kafka topics(s)!");
        }
        checkTopicsCreated();
    }

    private void checkTopicsCreated() throws KafkaClientException {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic: kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() throws KafkaClientException {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful() ) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
       try {
           return webClient.get()
                   .uri(kafkaConfigData.getSchemaRegistryUrl())
                   .exchange()
                   .map(ClientResponse::statusCode)
                   .block()
                   ;
       } catch (Exception e) {
           return  HttpStatus.SERVICE_UNAVAILABLE;
       }
    }

    private void sleep(Long sleepTimeMs) throws KafkaClientException {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topics(s)");
        }
    }

    private Boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        if (topics == null) return false;
        return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));
    }

    private void checkMaxRetry(int retry, Integer maxRetry) throws KafkaClientException {
        if (retry > maxRetry){
            throw new KafkaClientException("Reached max number of retry for reading kafka topics(s)");
        }
    }



    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating topics(s) {}, attempt {}", topicNames.size(), retryContext.getRetryCount());
        var kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumberOfPartitions(),
                kafkaConfigData.getReplicationFactor()))
                .collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() throws KafkaClientException {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Exception e) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topics(s)");
        }
        return topics;
    }

    private Collection<TopicListing>  doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        LOG.info("Reading kafka topic {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(new String[]{}), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
        }
        return topics;
    }
}
