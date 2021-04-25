package init;

import io.medalytics.microservice.demo.kafka.admin.exceptions.KafkaClientException;

public interface StreamInitializer {
    void init() throws KafkaClientException;
}
