package com.example.team258.kafka.repository;


import com.example.team258.kafka.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);

    Page<User> findAll(Pageable pageable);

    @Query("select u from users u " +
            "left join fetch u.bookRents br left join fetch br.book b " +
            "WHERE u.userId = :userId")
    Optional<User> findByIdFetchBookRent(@Param("userId") Long userId);

    @Query("select u from users u " +
            "left join fetch u.bookReservations br left join fetch br.book b " +
            "WHERE u.userId = :userId")
    Optional<User> findByIdFetchBookReservation(@Param("userId") Long userId);


    /**
     * 1대 다 관계라서 데이터 뻥튀기 가능
     */
    @Query("select u from users u " +
            "left join fetch u.bookApplyDonations bad " +
            "left join fetch bad.book " +
            "where u.userId = :userId")
    Optional<User> findFetchJoinById(@Param("userId") Long userId);

    @Query("select u from users u " +
            "left join fetch u.bookApplyDonations bad " +
            "left join fetch bad.book b " +
            "where u.userId = :userId")
    Optional<User> findFetchJoinBookById(@Param("userId") Long userId);

    @Query("SELECT u FROM users u WHERE u.userId = :userId")
    @QueryHints({ @QueryHint(name = "org.hibernate.fetchSize", value = "100") })
    Optional<User> findJPQLById(@Param("userId") Long userId);

}
