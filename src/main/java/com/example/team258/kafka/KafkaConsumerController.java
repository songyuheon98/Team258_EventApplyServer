package com.example.team258.kafka;

import com.example.team258.common.repository.UserRepository;
import com.example.team258.domain.member.dto.UserResponseDto;
import com.example.team258.kafka.dto.UserResponseKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class KafkaConsumerController {
//    @KafkaListener(topics = "bookDonationEventApplyOutput", groupId = "bookDonationEventApplyOutputConsumerGroup")
//    public String AdminUserManagementConsumer(String message) throws JsonProcessingException {
//
//        System.out.println("Received Message in group 'test-consumer-group': " + message);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        UserResponseKafkaDto userResponseDtos = objectMapper.readValue(message, UserResponseKafkaDto.class);
//        userResponseDtos.getUserResponseDtos().forEach(System.out::println);
//
//
//        return "adminV2";
//    }

}
