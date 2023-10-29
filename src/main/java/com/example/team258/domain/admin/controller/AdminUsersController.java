package com.example.team258.domain.admin.controller;

import com.example.team258.domain.admin.dto.AdminUsersResponseDto;
import com.example.team258.common.dto.MessageDto;
import com.example.team258.common.security.UserDetailsImpl;
import com.example.team258.domain.admin.service.AdminUsersService;
import com.example.team258.kafka.dto.AdminUserManagementDeleteKafkaDto;
import com.example.team258.kafka.dto.AdminUserManagementKafkaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUsersController {
    private final AdminUsersService adminUsersService;
    @GetMapping("/users")
    public ResponseEntity<List<AdminUsersResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminUsersService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<MessageDto> deleteUser(@PathVariable Long userId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        AdminUserManagementDeleteKafkaDto adminUserManagementDeleteKafkaDto = new AdminUserManagementDeleteKafkaDto(userId);
        return ResponseEntity.ok(adminUsersService.deleteUser(userId, userDetails.getUser()));
    }
}