package ru.netology.decisionservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.netology.brokerevents.events.ClaimEvent;
import ru.netology.brokerevents.events.DecisionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditCalcService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue.name}")
    private String messageQueue;

    public CreditCalcService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void processCreditClaim(ClaimEvent event) {
        BigDecimal monthlyPayment = event.getAmount().divide(BigDecimal.valueOf(event.getTerm()), 2, RoundingMode.HALF_UP);
        BigDecimal totalMonthlyPayment = monthlyPayment.add(event.getCurrentCreditLoad());
        Boolean approved = totalMonthlyPayment.compareTo(event.getIncome().multiply(BigDecimal.valueOf(0.5))) < 0;
        DecisionEvent dEvent = new DecisionEvent(event.getId(), approved);
        rabbitTemplate.convertAndSend(messageQueue, dEvent);
    }
}
