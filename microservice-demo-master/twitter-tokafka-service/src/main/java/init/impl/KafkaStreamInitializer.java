package init.impl;

import init.StreamInitializer;
import io.medalytics.microservice.demo.kafka.admin.client.KafkaAdminClient;
import io.medalytics.microservice.demo.kafka.admin.exceptions.KafkaClientException;
import io.medalytics.microservice.demo.common.config.KafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;

    public KafkaStreamInitializer(KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
    }

    @Override
    public void init() throws KafkaClientException {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        LOG.info("Topics with name {} ts ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
