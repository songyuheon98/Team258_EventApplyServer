package com.example.team258.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Qualifier("kafkaTemplate1")
    private final KafkaTemplate<String,String> kafkaTemplate;
    public void sendMessage(String topic, String message){
        kafkaTemplate.send(topic,message);
    }

}
