package com.example.team258.domain.donation.dto;

import com.example.team258.kafka.entity.BookApplyDonation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookApplyDonationResponseDto {
    private Long applyId;
    private LocalDateTime applyDate;
    private Long bookId;

    public BookApplyDonationResponseDto(BookApplyDonation bookApplyDonation) {
        this.applyId = bookApplyDonation.getApplyId();
        this.applyDate = bookApplyDonation.getApplyDate();
        this.bookId = bookApplyDonation.getBook().getBookId();
    }
}
