package com.example.team258.kafka.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoriesRequestDto {
    private String bookCategoryName;
    private Long bookCategoryIsbnCode;
}