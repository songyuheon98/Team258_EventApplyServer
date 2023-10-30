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

//    @KafkaListener(topics = "user-management-input-topic", groupId = "user-management-input-consumer-group")
//    public void AdminUserManagementConsume(String message) throws JsonProcessingException {
//        System.out.println("Received Message in group 'test-consumer-group1': " + message);
//
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        AdminUserManagementKafkaDto kafkaDto = objectMapper.readValue(message, AdminUserManagementKafkaDto.class);
//        Page<User> users = userService.findUsersByUsernameAndRoleV1(kafkaDto.getUserName(), kafkaDto.getUserRole()
//                , PageRequest.of(kafkaDto.getPage(), kafkaDto.getPageSize()));
//
//        List<UserResponseDto> userResponseDtos = users.stream().map(UserResponseDto::new).toList();
//        UserResponseKafkaDto userResponseKafkaDto = new UserResponseKafkaDto(userResponseDtos, kafkaDto.getPage(),users.getTotalPages()
//                ,kafkaDto.getCorrelationId());
//
//        String jsonString = objectMapper.writeValueAsString(userResponseKafkaDto);
//        producer.sendMessage("user-management-output-topic", jsonString);
//
//    }

    @KafkaListener(topics = "user-event-apply-input-topic", groupId = "user-management-input-consumer-group")
    public void AdminUserManagementConsume2(String message) throws JsonProcessingException {
        System.out.println("Received Message in group 'test-consumer-group2': " + message);

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
