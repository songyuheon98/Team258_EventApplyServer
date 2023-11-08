package com.example.team258.kafka.dto;

import com.example.team258.kafka.entity.BookCategory;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCategoriesResponseDto {
    private Long bookCategoryId;
    private Long bookCategoryIsbnCode;
    private String bookCategoryName;
    private Long parentCategoryId;

    public AdminCategoriesResponseDto(BookCategory bookCategory){
        this.bookCategoryId = bookCategory.getBookCategoryId();
        this.bookCategoryIsbnCode = bookCategory.getBookCategoryIsbnCode();
        this.bookCategoryName = bookCategory.getBookCategoryName();
        this.parentCategoryId = (bookCategory.getParentCategory() != null) ? bookCategory.getParentCategory().getBookCategoryId() : null;
    }
    public Long getParentCategory() {
        return this.parentCategoryId;
    }

    public String getBookCategoryIsbnCodeAndName() {
        if (parentCategoryId != null) {
            return bookCategoryIsbnCode + " - " + bookCategoryName + " (PCID :" + parentCategoryId + ")";
        } else {
            return bookCategoryIsbnCode + " - " + bookCategoryName;
        }
    }
}


