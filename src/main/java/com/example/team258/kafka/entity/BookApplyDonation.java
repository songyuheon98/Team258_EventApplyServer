package com.example.team258.kafka.entity;

import com.example.team258.domain.donation.dto.BookApplyDonationRequestDto;
import com.example.team258.common.entity.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;

@Entity(name = "book_apply_donation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookApplyDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long applyId;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    /**
     * orpanremoval 삭제 -> 삭제 취소
     * 도서 나눔 신청 취소할 시 도서 삭제되는 문제 발생 -> 도서와의 연관 관계 null로 변경하고 삭제하는 걸로 해결
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "bookApplyDonation", cascade = CascadeType.REMOVE)
    private Book book;

    public BookApplyDonation(BookApplyDonationRequestDto bookApplyDonationRequestDto) {
        this.applyDate = bookApplyDonationRequestDto.getApplyDate();
    }

    public void addBook(Book book){
        this.book = book;
        book.addBookApplyDonation(this);
    }
    public void removeBook(Book book){
        this.book = null;
        book.removeBookApplyDonation();
    }
}
