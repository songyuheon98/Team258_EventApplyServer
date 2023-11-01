package com.example.team258.domain.donation.service;

import com.example.team258.common.dto.BookResponseDto;
import com.example.team258.common.dto.BookResponsePageDto;
import com.example.team258.common.dto.MessageDto;
import com.example.team258.common.entity.Book;
import com.example.team258.common.entity.BookStatusEnum;
import com.example.team258.common.entity.QBook;
import com.example.team258.common.entity.User;
import com.example.team258.common.jwt.SecurityUtil;
import com.example.team258.common.repository.BookRepository;
import com.example.team258.common.repository.UserRepository;
import com.example.team258.domain.donation.dto.BookApplyDonationRequestDto;
import com.example.team258.domain.donation.dto.BookApplyDonationResponseDto;
import com.example.team258.domain.donation.dto.UserBookApplyCancelPageResponseDto;
import com.example.team258.domain.donation.entity.BookApplyDonation;
import com.example.team258.domain.donation.entity.BookDonationEvent;
import com.example.team258.domain.donation.repository.BookApplyDonationRepository;
import com.example.team258.domain.donation.repository.BookDonationEventRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookApplyDonationService {

    private final BookRepository bookRepository;
    private final BookDonationEventRepository bookDonationEventRepository;
    private final BookApplyDonationRepository bookApplyDonationRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageDto createBookApplyDonation(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
        Book book = getBook(bookApplyDonationRequestDto);

        MessageDto x = getMessageDto(book);
        if (x != null) return x;

        BookDonationEvent bookDonationEvent = getBookDonationEvent(bookApplyDonationRequestDto);

        MessageDto x1 = getMessageDto(bookDonationEvent);
        if (x1 != null) return x1;
        User user = getUser();
        BookApplyDonation bookApplyDonation = new BookApplyDonation(bookApplyDonationRequestDto);

        bookApplyDonationRepository.save(bookApplyDonation);
        bookApplyDonation.addBook(book);

        user.getBookApplyDonations().add(bookApplyDonation);
        bookDonationEvent.getBookApplyDonations().add(bookApplyDonation);
        book.changeStatus(BookStatusEnum.SOLD_OUT);
        return new MessageDto("책 나눔 신청이 완료되었습니다.");
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

    private User getUser() {
        User user = userRepository.findFetchJoinById(SecurityUtil.getPrincipal().get().getUserId()).orElseThrow(
                ()->new IllegalArgumentException("해당 사용자는 도서관 사용자가 아닙니다.")
        );
        return user;
    }

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

    @Transactional
    public MessageDto deleteBookApplyDonation(Long applyId) {
        BookApplyDonation bookApplyDonation = bookApplyDonationRepository.findById(applyId)
                .orElseThrow(()->new IllegalArgumentException("해당 신청이 존재하지 않습니다."));

        bookApplyDonation.getBook().changeStatus(BookStatusEnum.DONATION);

        bookApplyDonation.removeBook(bookApplyDonation.getBook());

        bookApplyDonationRepository.delete(bookApplyDonation);

        return new MessageDto("책 나눔 신청이 취소되었습니다.");
    }

    public List<BookResponseDto> getDonationBooks(BookStatusEnum bookStatus) {

        List<BookResponseDto> bookResponseDtos= bookRepository.findByBookStatus(bookStatus).stream()
                .map(BookResponseDto::new)
                .toList();
        return bookResponseDtos;
    }
    public BookResponsePageDto getDonationBooksV2(BookStatusEnum bookStatus, Pageable pageable) {
        Page<Book> pageBooks = bookRepository.findPageByBookStatus(bookStatus,pageable);
        List<BookResponseDto> bookResponseDtos= pageBooks.stream()
                .map(BookResponseDto::new)
                .toList();

        return new BookResponsePageDto(bookResponseDtos, pageBooks.getTotalPages());
    }
    public BookResponsePageDto getDonationBooksV3(BookStatusEnum bookStatusEnum, PageRequest pageRequest, BookResponseDto bookResponseDto) {
        QBook qBook = QBook.book;
        BooleanBuilder builder = new BooleanBuilder();

        if(bookResponseDto.getBookId()!=null && !bookResponseDto.getBookId().equals(""))
            builder.and(qBook.bookId.eq(bookResponseDto.getBookId()));

        if(bookResponseDto.getBookName()!=null && !bookResponseDto.getBookName().equals(""))
            builder.and(qBook.bookName.contains(bookResponseDto.getBookName()));
        if(bookResponseDto.getBookAuthor()!=null && !bookResponseDto.getBookAuthor().equals(""))
            builder.and(qBook.bookAuthor.contains(bookResponseDto.getBookAuthor()));
        if(bookResponseDto.getBookPublish()!=null && !bookResponseDto.getBookPublish().equals(""))
            builder.and(qBook.bookPublish.contains(bookResponseDto.getBookPublish()));
        builder.and(qBook.bookStatus.eq(bookStatusEnum));

        Page<Book> pageBooks = bookRepository.findAll(builder, pageRequest);

        List<BookResponseDto> bookResponseDtos= pageBooks.stream()
                .map(BookResponseDto::new)
                .toList();

        return new BookResponsePageDto(bookResponseDtos, pageBooks.getTotalPages());
    }

    public List<BookApplyDonationResponseDto> getBookApplyDonations() {
        return bookApplyDonationRepository.findAll().stream()
                .map(bookApplyDonation -> new BookApplyDonationResponseDto(bookApplyDonation))
                .toList();
    }


    public UserBookApplyCancelPageResponseDto getDonationBooksCancel() {
        Long userId = SecurityUtil.getPrincipal().get().getUserId();
        User user = getUser();
        return new UserBookApplyCancelPageResponseDto(user);
    }

    //V2 : 낙관적락/비관적락
    @Transactional
    public MessageDto createBookApplyDonationV2(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
//        Book book = bookRepository.findById(bookApplyDonationRequestDto.getBookId()) //비관적락 미적용
        Book book = bookRepository.findByIdFetch(bookApplyDonationRequestDto.getBookId()) //비관적락 적용
                .orElseThrow(()->new IllegalArgumentException("나눔 신청한 책이 존재하지 않습니다."));
        MessageDto x = getMessageDto(book);
        if (x != null) return x;
        BookDonationEvent bookDonationEvent = getBookDonationEvent(bookApplyDonationRequestDto);
        MessageDto x1 = getMessageDto(bookDonationEvent);
        if (x1 != null) return x1;
        User user = getUser();
        BookApplyDonation bookApplyDonation = new BookApplyDonation(bookApplyDonationRequestDto);
        bookApplyDonationRepository.save(bookApplyDonation);
        bookApplyDonation.addBook(book);
        user.getBookApplyDonations().add(bookApplyDonation);
        bookDonationEvent.getBookApplyDonations().add(bookApplyDonation);
        book.changeStatus(BookStatusEnum.SOLD_OUT);
        return new MessageDto("책 나눔 신청이 완료되었습니다.");
    }
    //V3 : Transactional Serializable
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MessageDto createBookApplyDonationV3(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
        Book book = getBook(bookApplyDonationRequestDto);
        MessageDto x = getMessageDto(book);
        if (x != null) return x;
        BookDonationEvent bookDonationEvent = getBookDonationEvent(bookApplyDonationRequestDto);
        if(LocalDateTime.now().isBefore(bookDonationEvent.getCreatedAt()) ||
                LocalDateTime.now().isAfter( bookDonationEvent.getClosedAt())){
            throw new IllegalArgumentException("책 나눔 이벤트 기간이 아닙니다.");
        }
        User user = getUser();
        BookApplyDonation bookApplyDonation = new BookApplyDonation(bookApplyDonationRequestDto);
        bookApplyDonationRepository.save(bookApplyDonation);
        bookApplyDonation.addBook(book);
        user.getBookApplyDonations().add(bookApplyDonation);
        bookDonationEvent.getBookApplyDonations().add(bookApplyDonation);
        book.changeStatus(BookStatusEnum.SOLD_OUT);
        return new MessageDto("책 나눔 신청이 완료되었습니다.");
    }
    /**
     * RedissonConfig를 활성화할 경우 현재 github action의 테스트코드 실행시 redis를 실행하지 못해 오류가 발생함
     * 따라서 redisson 분산락을 사용하는 아래의 코드 일부와 common-config-RedissonConfig 파일 전문을 주석처리한 상태임
     */
    //V4 : Redisson 사용
//    public MessageDto createBookApplyDonationV4(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
////        RLock lock = redissonClient.getLock(String.valueOf(bookApplyDonationRequestDto.getBookId()));
////
////        try {
////            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
////                log.info("락 획득 실패");
////                throw new IllegalArgumentException("락 획득 실패");
////            }
////            log.info("락 획득 성공");
//        bookApplyDonationService2.createBookApplyDonation(bookApplyDonationRequestDto);
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////            e.printStackTrace();
////        } finally {
////            log.info("finally문 실행");
////            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
////                lock.unlock();
////                log.info("언락 실행");
////            }
////        }
//        return new MessageDto("책 나눔 신청이 완료되었습니다.");
//    }

}

