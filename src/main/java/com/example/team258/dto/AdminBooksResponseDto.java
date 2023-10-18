package com.example.team258.dto;

import com.example.team258.entity.Book;
import com.example.team258.entity.BookCategory;
import com.example.team258.entity.BookStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminBooksResponseDto {
    private Long bookId;
    private String bookName;
    private String bookAuthor;
    private String bookPublish;
    private BookStatusEnum bookStatus;
    private Long bookCategoryId;

    // 추가: bookCategory 필드
    private BookCategory bookCategory;

    public AdminBooksResponseDto(Book book) {
        this.bookId = book.getBookId();
        this.bookName = book.getBookName();
        this.bookAuthor = book.getBookAuthor();
        this.bookPublish = book.getBookPublish();
        this.bookStatus = book.getBookStatus();
        this.bookCategoryId = book.getBookCategory().getBookCategoryId();
        // 카테고리얻어오기위해추가
        this.bookCategory = book.getBookCategory();
    }

    // 추가: bookCategory를 반환하는 메서드
    public String getBookCategoryIsbnCodeAndName() {
        if (bookCategory != null) {
            return bookCategory.getBookCategoryIsbnCode() + "-" + bookCategory.getBookCategoryName();
        } else {
            return "";
        }
    }
}