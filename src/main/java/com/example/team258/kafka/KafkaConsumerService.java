package com.example.team258.kafka;

import com.example.team258.kafka.service.BookApplyDonationService;
import com.example.team258.kafka.dto.MessageKafkaDto;
import com.example.team258.kafka.dto.UserEventApplyKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final KafkaProducerService producer;
    private final BookApplyDonationService bookApplyDonationService;
    private final RedissonClient redissonClient;

    @KafkaListener(topics = "user-event-apply-input-topic", groupId = "user-event-apply-consumer-group${GROUP_ID}",
    containerFactory = "kafkaListenerContainerFactory2")
    public void AdminUserEventApplyConsume2(String message) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserEventApplyKafkaDto kafkaDto = objectMapper.readValue(message, UserEventApplyKafkaDto.class);

        RLock lock = redissonClient.getLock(String.valueOf(kafkaDto.getBookApplyDonationRequestDto().getBookId()));

        MessageKafkaDto messageKafkaDto = new MessageKafkaDto();

        try {
            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("락 획득 실패");
            }

            messageKafkaDto =new MessageKafkaDto(bookApplyDonationService.createBookApplyDonationKafka(kafkaDto.getBookApplyDonationRequestDto(),
                    kafkaDto.getUserId()), kafkaDto.getCorrelationId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        String jsonString = objectMapper.writeValueAsString(messageKafkaDto);
        producer.sendMessage("user-event-apply-output-topic", jsonString);
    }
}
