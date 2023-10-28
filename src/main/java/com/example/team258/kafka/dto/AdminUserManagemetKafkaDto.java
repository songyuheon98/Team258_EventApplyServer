package com.example.team258.kafka.dto;

import lombok.Data;

@Data
public class AdminUserManagemetKafkaDto {

    private String userName;
    private String userRole;
    private int page;
    private int pageSize;
    public AdminUserManagemetKafkaDto(String userName, String userRole, int page, int pageSize) {
        this.userName = userName;
        this.userRole = userRole;
        this.page = page;
        this.pageSize = pageSize;
    }
}
