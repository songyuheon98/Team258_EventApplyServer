package com.example.team258.common.controller.mixedController.admin;

import com.example.team258.common.repository.UserRepository;
import com.example.team258.common.service.UserService;
import com.example.team258.kafka.KafkaProducerService;
import com.example.team258.kafka.dto.AdminUserManagementKafkaDto;
import com.example.team258.kafka.dto.UserResponseKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Controller
@RequiredArgsConstructor
public class AdminUsersMixedController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final KafkaProducerService producer;

    private final Map<String, CompletableFuture<UserResponseKafkaDto>> futures = new ConcurrentHashMap<>();

    /**
     * 관리자 - 사용자 관리 페이지
     * @param page : 현재 페이지
     * @param model : 뷰에 전달할 데이터
     * @return : 뷰
     */
    /**
     * @RequestParam(defaultValue = "0") int page : page 파라미터가 없으면 0으로 설정
     */
    @GetMapping("/admin/users/v2")
    public String adminViewV2(@RequestParam(defaultValue = "0") int page, Model model, @RequestParam(defaultValue = "") String userName
            ,@RequestParam(defaultValue = "") String userRole, @RequestParam(defaultValue = "5") int pageSize ) throws JsonProcessingException, ExecutionException, InterruptedException {

        AdminUserManagementKafkaDto adminUserManagementKafkaDto = new AdminUserManagementKafkaDto(userName, userRole, page, pageSize);
        String correlationId = UUID.randomUUID().toString();
        adminUserManagementKafkaDto.setCorrelationId(correlationId);


        ObjectMapper objectMapper = new ObjectMapper();

        String jsonString = objectMapper.writeValueAsString(adminUserManagementKafkaDto);

        CompletableFuture<UserResponseKafkaDto> future = new CompletableFuture<>();
        futures.put(correlationId, future);

        producer.sendMessage("user-management-input-topic",jsonString);

        UserResponseKafkaDto userResponseDtos = future.get(); // 결과를 기다립니다.

        model.addAttribute("currentPage", userResponseDtos.getPage());  // 현재 페이지 번호 추가
        model.addAttribute("totalPages", userResponseDtos.getTotalPages());
        model.addAttribute("users", userResponseDtos.getUserResponseDtos());

        return "adminV2";
    }

    @KafkaListener(topics = "user-management-output-topic", groupId = "user-management-output-consumer-group")
    public void AdminUserManagementConsumer(String message) throws JsonProcessingException {
        System.out.println("Received Message in group 'test-consumer-group': " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        UserResponseKafkaDto userResponseDtos = objectMapper.readValue(message, UserResponseKafkaDto.class);
        CompletableFuture<UserResponseKafkaDto> future = futures.get(userResponseDtos.getCorrelationId());

        if (future != null) {
            future.complete(userResponseDtos);
        }
    }

}