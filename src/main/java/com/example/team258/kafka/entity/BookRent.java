package com.example.team258.kafka.entity;

import com.example.team258.common.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "book_rent")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookRent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookRent_id")
    private Long bookRentId;

    @Column
    private LocalDateTime returnDate;

    @OneToOne(mappedBy = "bookRent",fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public BookRent(Book book) {
        this.book = book;
        this.returnDate = LocalDateTime.now().plusDays(7);
    }
}
