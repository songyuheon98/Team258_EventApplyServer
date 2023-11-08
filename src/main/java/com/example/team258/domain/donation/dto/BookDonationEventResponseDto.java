package com.example.team258.domain.donation.dto;

import com.example.team258.common.dto.BookResponseDto;
import com.example.team258.kafka.entity.BookDonationEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BookDonationEventResponseDto {
    private Long donationId;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private List<BookResponseDto> bookResponseDtos;

    public BookDonationEventResponseDto(BookDonationEvent bookDonationEvent){
        this.donationId = bookDonationEvent.getDonationId();
        this.createdAt = bookDonationEvent.getCreatedAt();
        this.closedAt = bookDonationEvent.getClosedAt();
        if(bookDonationEvent.getBooks() != null)
            this.bookResponseDtos = bookDonationEvent.getBooks().stream().map(BookResponseDto::new).toList();
    }
}
