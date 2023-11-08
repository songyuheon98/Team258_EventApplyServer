package com.example.team258.kafka.repository;

import com.example.team258.common.entity.Book;
import com.example.team258.common.entity.BookCategory;
import com.example.team258.kafka.repository.BookRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.stereotype.Repository;

import static com.example.team258.common.entity.QBook.book;


@RequiredArgsConstructor
@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Book> findAllByCategoriesAndBookNameContaining2(List<BookCategory> bookCategories, String keyword, Pageable pageable) {
        JPQLQuery<Book> query = jpaQueryFactory
                .selectFrom(book)
                .where(bookCategoriesEq(bookCategories), keywordEq(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Book> books = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(books, pageable, totalCount);
    }

    private BooleanExpression bookCategoriesEq(List<BookCategory> bookCategories) {
        return bookCategories != null ? book.bookCategory.in(bookCategories) : null;
    }

    private BooleanExpression keywordEq(String keyword) {
        return keyword != null ? book.bookName.like("%"+keyword+"%") : null;
    }
}
