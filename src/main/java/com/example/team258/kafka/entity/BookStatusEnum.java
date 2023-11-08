package com.example.team258.kafka.entity;

public enum BookStatusEnum {

    POSSIBLE(BookStatus.POSSIBLE),  // 대여 가능 상태
    IMPOSSIBLE(BookStatus.IMPOSSIBLE),  // 대여 불가능 상태
    DONATION(BookStatus.DONATION), // 기부 이벤트 등록 가능 상태
    SOLD_OUT(BookStatus.SOLD_OUT); // 판매 완료 상태


    private final String bookStatus;

    BookStatusEnum(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookStatus() {
        return this.bookStatus;
    }

    public static class BookStatus {
        public static final String POSSIBLE = "POSSIBLE";
        public static final String IMPOSSIBLE = "IMPOSSIBLE";
        public static final String DONATION = "DONATION";
        public static final String SOLD_OUT = "SOLD_OUT";
    }
}
