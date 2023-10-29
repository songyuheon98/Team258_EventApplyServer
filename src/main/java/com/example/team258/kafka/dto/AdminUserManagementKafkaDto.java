package com.example.team258.kafka.dto;

import lombok.Data;
import lombok.Setter;

@Data
public class AdminUserManagementKafkaDto {

    private String userName;
    private String userRole;
    private int page;
    private int pageSize;
    private String correlationId;
    public AdminUserManagementKafkaDto(String userName, String userRole, int page, int pageSize) {
        this.userName = userName;
        this.userRole = userRole;
        this.page = page;
        this.pageSize = pageSize;
    }
}
