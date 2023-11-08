package com.example.team258.domain.donation.repository;

import com.example.team258.kafka.entity.BookDonationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookDonationEventRepository extends JpaRepository<BookDonationEvent, Long>, QuerydslPredicateExecutor<BookDonationEvent>   {
    Page<BookDonationEvent> findAll(Pageable pageable);

    @Query("select bde from book_donation_event bde " +
            "left join fetch bde.bookApplyDonations bad " +
            "left join fetch bad.book b " +
            "where bde.donationId = :donationId")
    Optional<BookDonationEvent> findFetchJoinById(@Param("donationId") Long donationId);


}
