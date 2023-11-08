package com.example.team258.kafka.entity;

import com.example.team258.common.entity.Book;
import com.example.team258.common.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "book_reservation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_reservation_id")
    private Long bookReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_id")
    private Book book;

    public BookReservation(User savedUser, Book book) {
        this.user = savedUser;
        this.book = book;
    }

}
