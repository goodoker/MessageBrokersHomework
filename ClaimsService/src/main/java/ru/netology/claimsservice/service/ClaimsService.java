package ru.netology.claimsservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.netology.claimsservice.dto.CreditClaim;
import ru.netology.claimsservice.entity.ClaimEntity;
import ru.netology.brokerevents.events.ClaimEvent;
import ru.netology.brokerevents.events.DecisionEvent;
import ru.netology.claimsservice.model.ClaimStatus;
import ru.netology.claimsservice.repository.ClaimsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimsService {
    private ClaimsRepository claimsRepository;

    //храним имя топика тут
    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopicName;

    private final KafkaTemplate<String, ClaimEvent> kafkaTemplate;

    //конструктор сервиса
    @Autowired
    public ClaimsService(ClaimsRepository claimsRepository, KafkaTemplate<String, ClaimEvent> kafkaTemplate) {
        this.claimsRepository = claimsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //сохранить заявку в БД, создать event и пихнуть в кафку
    public Long processClaim(CreditClaim claim) {
        //получаем в параметры DTO, из него создаём сущность для сохранения в БД
        ClaimEntity entity = new ClaimEntity(claim);
        claimsRepository.saveAndFlush(entity);

        //из сущности для БД создаём сущность для передачи в кафку
        ClaimEvent event = new ClaimEvent();
        event.setId(entity.getId());
        event.setAmount(entity.getAmount());
        event.setTerm(entity.getTerm());
        event.setIncome(entity.getIncome());
        event.setCurrentCreditLoad(entity.getCurrentCreditLoad());
        event.setCurrentCreditRating(entity.getCurrentCreditRating());

        //пуляем Event в кафку
        kafkaTemplate.send(kafkaTopicName, event);
        //возвращаем id сущности из БД
        return entity.getId();
    }

    //обработка ответа от DecisionService
    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void processClaimDecision(DecisionEvent event) {
        //вынимаем id из ответа
        Long id = event.getId();
        //конструируем статус на основе Boolean поля ответа
        ClaimStatus status = event.getDecision() ? ClaimStatus.APPROVED : ClaimStatus.REJECTED;
        //обновляем статус заявки в БД, если она есть
        Optional<ClaimEntity> optEntity = claimsRepository.findById(id);
        if (optEntity.isPresent()) {
            ClaimEntity entity = optEntity.get();
            entity.setStatus(status);
            claimsRepository.saveAndFlush(entity);
        }
    }

    //получить заявку по id
    public Optional<ClaimEntity> getClaimById(Long id) {
        return claimsRepository.findById(id);
    }

    //получить список всех заявок
    public List<ClaimEntity> getAllClaims() {
        List<ClaimEntity> entityList = claimsRepository.findAll();
        return entityList;
    }
}