package com.example.team258.kafka.repository;

import com.example.team258.kafka.entity.BookCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, QuerydslPredicateExecutor<BookCategory> {

    BookCategory findByBookCategoryName(String bookCategoryName);

    Page<BookCategory> findAll(Specification<BookCategory> spec, Pageable pageable);
}
