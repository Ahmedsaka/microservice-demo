package io.medalytics.demo.service;

import io.medalytics.demo.config.MessagingConfig;
import io.medalytics.demo.model.EmailTemplate;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void sendMessage(EmailTemplate emailTemplate) {
        rabbitTemplate.convertAndSend(MessagingConfig.EMAIL_EXCHANGE, MessagingConfig.EMAIL_ROUTING_KEY, emailTemplate);
    }

}
