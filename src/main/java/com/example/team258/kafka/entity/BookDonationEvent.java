package com.example.team258.kafka.entity;

import com.example.team258.kafka.dto.BookDonationEventRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "book_donation_event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookDonationEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="donation_id")
    private Long donationId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="closed_at")
    private LocalDateTime closedAt;

    // 단 방향
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name="donation_id")
    private List<BookApplyDonation> bookApplyDonations=new ArrayList<>();

    /**
     * 양방향
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bookDonationEvent", cascade = CascadeType.REMOVE)
    private List<Book> books = new ArrayList<>();

    public BookDonationEvent(BookDonationEventRequestDto bookDonationEventRequestDto){
        this.createdAt = bookDonationEventRequestDto.getCreatedAt();
        this.closedAt = bookDonationEventRequestDto.getClosedAt();
    }

    public void update(BookDonationEventRequestDto bookDonationEventRequestDto) {
        this.createdAt = bookDonationEventRequestDto.getCreatedAt();
        this.closedAt = bookDonationEventRequestDto.getClosedAt();
    }

    public void addBook(Book book){
        this.books.add(book);
        book.addBookDonationEvent(this);
    }
    public void removeBook(Book book){
        this.books.remove(book);
        book.removeBookDonationEvent();
    }

}
