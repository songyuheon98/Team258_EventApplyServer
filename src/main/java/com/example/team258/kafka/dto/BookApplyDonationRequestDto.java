package com.example.team258.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookApplyDonationRequestDto {
    private Long donationId;
    private Long bookId;
    private LocalDateTime applyDate;
}
