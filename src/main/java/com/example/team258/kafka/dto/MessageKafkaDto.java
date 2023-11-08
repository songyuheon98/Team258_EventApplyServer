package com.example.team258.kafka.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageKafkaDto {
    private MessageDto messageDto;
    private String correlationId;
    public MessageKafkaDto(MessageDto messageDto, String correlationId) {
        this.messageDto = messageDto;
        this.correlationId = correlationId;
    }
}
