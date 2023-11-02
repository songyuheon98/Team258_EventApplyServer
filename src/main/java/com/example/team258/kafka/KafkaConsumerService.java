package com.example.team258.kafka;

import com.example.team258.common.service.UserService;
import com.example.team258.domain.donation.service.BookApplyDonationService;
import com.example.team258.kafka.dto.MessageKafkaDto;
import com.example.team258.kafka.dto.UserEventApplyKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final UserService userService;
    private final KafkaProducerService producer;
    private final BookApplyDonationService bookApplyDonationService;


    @KafkaListener(topics = "user-event-apply-input-topic", groupId = "user-event-apply-consumer-group2",
    containerFactory = "kafkaListenerContainerFactory2")
    public void AdminUserEventApplyConsume2(String message) throws JsonProcessingException {
//        System.out.println("Received Message in group 'test-consumer-group2': " + message);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        UserEventApplyKafkaDto kafkaDto = objectMapper.readValue(message, UserEventApplyKafkaDto.class);

        MessageKafkaDto messageKafkaDto =
                new MessageKafkaDto(bookApplyDonationService.createBookApplyDonationKafka(kafkaDto.getBookApplyDonationRequestDto(),
                        kafkaDto.getUserId()), kafkaDto.getCorrelationId());

        String jsonString = objectMapper.writeValueAsString(messageKafkaDto);
        producer.sendMessage("user-event-apply-output-topic", jsonString);

    }
}
