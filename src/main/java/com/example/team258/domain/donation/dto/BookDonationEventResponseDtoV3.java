package com.example.team258.domain.donation.dto;
import com.example.team258.common.dto.BookResponseDto;
import com.example.team258.kafka.entity.BookDonationEvent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookDonationEventResponseDtoV3 {
    private Long donationId;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private List<BookResponseDto> bookResponseDtos;
    public BookDonationEventResponseDtoV3(BookDonationEvent t) {
        this.donationId = t.getDonationId();
        this.createdAt = t.getCreatedAt();
        this.closedAt = t.getClosedAt();
    }
    public BookDonationEventResponseDtoV3(Long donationId, LocalDateTime createdAt, LocalDateTime closedAt) {
        this.donationId = donationId;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }
}
