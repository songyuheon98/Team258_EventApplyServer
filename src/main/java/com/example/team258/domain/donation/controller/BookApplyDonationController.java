package com.example.team258.domain.donation.controller;

import com.example.team258.common.dto.BookResponseDto;
import com.example.team258.common.dto.MessageDto;
import com.example.team258.common.entity.BookStatusEnum;
import com.example.team258.common.jwt.SecurityUtil;
import com.example.team258.domain.donation.dto.BookApplyDonationRequestDto;
import com.example.team258.domain.donation.dto.BookApplyDonationResponseDto;
import com.example.team258.domain.donation.service.BookApplyDonationService;
import com.example.team258.kafka.KafkaProducerService;
import com.example.team258.kafka.dto.AdminUserManagementKafkaDto;
import com.example.team258.kafka.dto.MessageKafkaDto;
import com.example.team258.kafka.dto.UserEventApplyKafkaDto;
import com.example.team258.kafka.dto.UserResponseKafkaDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class BookApplyDonationController {

    private final BookApplyDonationService bookApplyDonationService;
    private final KafkaProducerService producer;
    private final Map<String, CompletableFuture<MessageKafkaDto>> futures = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Semaphore semaphore;

    @DeleteMapping("/bookApplyDonation/{applyId}")
    public ResponseEntity<MessageDto> deleteBookApplyDonation(@PathVariable Long applyId){

        return ResponseEntity.ok().body(bookApplyDonationService.deleteBookApplyDonation(applyId));
    }

    /**
     * 책 기부 신청 목록 조회
     * @param bookStatus
     * @return
     */
    private final Lock lock=new ReentrantLock();

    @PostMapping("/bookApplyDonation")
    public ResponseEntity<MessageDto> createBookApplyDonation(@RequestBody BookApplyDonationRequestDto bookApplyDonationRequestDto){
        try{
            lock.lock();
            return ResponseEntity.ok().body(bookApplyDonationService.createBookApplyDonation(bookApplyDonationRequestDto));
        }
        finally {
            lock.unlock();
        }
    }

    @PostMapping("/bookApplyDonation/semaphore")
    public ResponseEntity<MessageDto> createBookApplyDonationSemaphore(@RequestBody BookApplyDonationRequestDto bookApplyDonationRequestDto){
        try{
            semaphore.acquire();
            return ResponseEntity.ok().body(bookApplyDonationService.createBookApplyDonation(bookApplyDonationRequestDto));
        }catch (InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageDto("나눔 신청에 실패했습니다."));
        }finally {
            semaphore.release();
        }
    }
    //낙관적락,비관적락 사용시
    @PostMapping("/bookApplyDonation/v2")
    public ResponseEntity<MessageDto> createBookApplyDonationV2(@RequestBody BookApplyDonationRequestDto bookApplyDonationRequestDto){
        return ResponseEntity.ok().body(bookApplyDonationService.createBookApplyDonationV2(bookApplyDonationRequestDto));
    }

    //Transactional SERIALIZable 사용
    @PostMapping("/bookApplyDonation/v3")
    public ResponseEntity<MessageDto> createBookApplyDonationV3(@RequestBody BookApplyDonationRequestDto bookApplyDonationRequestDto){
        return ResponseEntity.ok().body(bookApplyDonationService.createBookApplyDonationV3(bookApplyDonationRequestDto));
    }

//    //redisson 분산락 사용
//    @PostMapping("/bookApplyDonation/v4")
//    public ResponseEntity<MessageDto> createBookApplyDonationV4(@RequestBody BookApplyDonationRequestDto bookApplyDonationRequestDto){
//        return ResponseEntity.ok().body(bookApplyDonationService.createBookApplyDonationV4(bookApplyDonationRequestDto));
//    }
}

