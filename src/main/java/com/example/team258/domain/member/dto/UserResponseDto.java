package com.example.team258.domain.member.dto;

import com.example.team258.common.entity.User;
import com.example.team258.common.entity.UserRoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String username;
    private UserRoleEnum role;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

}
