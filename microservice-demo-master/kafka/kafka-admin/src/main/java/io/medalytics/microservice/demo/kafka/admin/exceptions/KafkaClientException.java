package io.medalytics.microservice.demo.kafka.admin.exceptions;

public class KafkaClientException extends Throwable {
    public KafkaClientException(String message) {
        super(message);
    }
}
