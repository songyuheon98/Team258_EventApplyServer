package com.example.team258.kafka.dto;

import com.example.team258.domain.donation.dto.BookApplyDonationRequestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEventApplyKafkaDto {
    private String correlationId;
    private Long userId;
    private BookApplyDonationRequestDto bookApplyDonationRequestDto;
    public UserEventApplyKafkaDto(BookApplyDonationRequestDto bookApplyDonationRequestDto, Long userId) {
        this.bookApplyDonationRequestDto = bookApplyDonationRequestDto;
        this.userId = userId;
    }
}
