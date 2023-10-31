package com.example.team258.common.controller.mixedController.admin;

import com.example.team258.common.entity.User;
import com.example.team258.common.repository.UserRepository;
import com.example.team258.common.service.UserService;
import com.example.team258.domain.member.dto.UserResponseDto;
import com.example.team258.kafka.KafkaProducerService;
import com.example.team258.kafka.dto.AdminUserManagementKafkaDto;
import com.example.team258.kafka.dto.UserResponseKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

        Page<User> users = userService.findUsersByUsernameAndRoleV1(userName, userRole, PageRequest.of(page, pageSize));

        List<UserResponseDto> userResponseDtos = users.stream().map(UserResponseDto::new).collect(Collectors.toList());

        model.addAttribute("currentPage", page);  // 현재 페이지 번호 추가
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("users", userResponseDtos);

        return "adminV2";
    }
}