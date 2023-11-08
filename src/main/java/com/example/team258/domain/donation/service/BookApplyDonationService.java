package com.example.team258.domain.donation.service;

import com.example.team258.common.dto.MessageDto;
import com.example.team258.common.entity.Book;
import com.example.team258.common.entity.BookStatusEnum;
import com.example.team258.common.entity.User;
import com.example.team258.common.repository.BookRepository;
import com.example.team258.common.repository.UserRepository;
import com.example.team258.domain.donation.dto.BookApplyDonationRequestDto;
import com.example.team258.domain.donation.dto.BookApplyDonationResponseDto;
import com.example.team258.domain.donation.entity.BookApplyDonation;
import com.example.team258.domain.donation.entity.BookDonationEvent;
import com.example.team258.domain.donation.repository.BookApplyDonationRepository;
import com.example.team258.domain.donation.repository.BookDonationEventRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookApplyDonationService {

    private final BookRepository bookRepository;
    private final BookDonationEventRepository bookDonationEventRepository;
    private final BookApplyDonationRepository bookApplyDonationRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageDto createBookApplyDonationKafka(BookApplyDonationRequestDto bookApplyDonationRequestDto,Long userId) {
        Book book = getBook(bookApplyDonationRequestDto);
        MessageDto x = getMessageDto(book);
        if (x != null) return x;
        BookDonationEvent bookDonationEvent = getBookDonationEvent(bookApplyDonationRequestDto);

        MessageDto x1 = getMessageDto(bookDonationEvent);
        if (x1 != null) return x1;
        User user = userRepository.findFetchJoinById(userId).orElseThrow(
                ()->new IllegalArgumentException("해당 사용자는 도서관 사용자가 아닙니다.")
        );
        BookApplyDonation bookApplyDonation = new BookApplyDonation(bookApplyDonationRequestDto);
        bookApplyDonationRepository.save(bookApplyDonation);
        bookApplyDonation.addBook(book);

        user.getBookApplyDonations().add(bookApplyDonation);
        bookDonationEvent.getBookApplyDonations().add(bookApplyDonation);
        book.changeStatus(BookStatusEnum.SOLD_OUT);

        return new MessageDto("책 나눔 신청이 완료되었습니다.");
    }

    private Book getBook(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
        Book book = bookRepository.findById(bookApplyDonationRequestDto.getBookId())
                .orElseThrow(()->new IllegalArgumentException("나눔 신청한 책이 존재하지 않습니다."));
        return book;
    }


    public List<BookApplyDonationResponseDto> getBookApplyDonations() {
        return bookApplyDonationRepository.findAll().stream()
                .map(bookApplyDonation -> new BookApplyDonationResponseDto(bookApplyDonation))
                .toList();
    }

    private MessageDto getMessageDto(BookDonationEvent bookDonationEvent) {
        if(LocalDateTime.now().isBefore(bookDonationEvent.getCreatedAt()) ||
                LocalDateTime.now().isAfter( bookDonationEvent.getClosedAt())){
            return new MessageDto("책 나눔 이벤트 기간이 아닙니다.");
        }
        return null;
    }

    private BookDonationEvent getBookDonationEvent(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
        BookDonationEvent bookDonationEvent = bookDonationEventRepository.findFetchJoinById(bookApplyDonationRequestDto.getDonationId())
                .orElseThrow(()->new IllegalArgumentException("해당 이벤트가 존재하지 않습니다."));
        return bookDonationEvent;
    }

    private MessageDto getMessageDto(Book book) {
        if(book.getBookApplyDonation()!=null){
            return new MessageDto("이미 누군가 먼저 신청했습니다.");
        }
        return null;
    }
}

