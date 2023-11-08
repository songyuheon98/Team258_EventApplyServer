package com.example.team258.kafka.dto;

import com.example.team258.kafka.entity.BookStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBooksRequestDto {
    private String bookName;
    private String bookAuthor;
    private String bookPublish;
    private Long bookCategoryId;
    private BookStatusEnum bookStatus;
}
