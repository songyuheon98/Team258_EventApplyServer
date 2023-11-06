package com.example.team258.kafka;

import com.example.team258.common.dto.MessageDto;
import com.example.team258.common.service.UserService;
import com.example.team258.domain.donation.service.BookApplyDonationService;
import com.example.team258.kafka.dto.MessageKafkaDto;
import com.example.team258.kafka.dto.UserEventApplyKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final UserService userService;
    private final KafkaProducerService producer;
    private final BookApplyDonationService bookApplyDonationService;
    private final RedissonClient redissonClient;

    @KafkaListener(topics = "user-event-apply-input-topic", groupId = "user-event-apply-consumer-group${GROUP_ID}",
    containerFactory = "kafkaListenerContainerFactory2")
    public void AdminUserEventApplyConsume2(String message) throws JsonProcessingException {
//        System.out.println("Received Message in group 'test-consumer-group2': " + message);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserEventApplyKafkaDto kafkaDto = objectMapper.readValue(message, UserEventApplyKafkaDto.class);

        RLock lock = redissonClient.getLock(String.valueOf(kafkaDto.getBookApplyDonationRequestDto().getBookId()));

        MessageKafkaDto messageKafkaDto = new MessageKafkaDto();

        try {
            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
//                log.info("락 획득 실패");
                throw new IllegalArgumentException("락 획득 실패");
            }
//            log.info("락 획득 성공");

            messageKafkaDto =new MessageKafkaDto(bookApplyDonationService.createBookApplyDonationKafka(kafkaDto.getBookApplyDonationRequestDto(),
                    kafkaDto.getUserId()), kafkaDto.getCorrelationId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
//            log.info("finally문 실행");
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
//                log.info("언락 실행");
            }
        }
        String jsonString = objectMapper.writeValueAsString(messageKafkaDto);
        producer.sendMessage("user-event-apply-output-topic", jsonString);
    }
}
