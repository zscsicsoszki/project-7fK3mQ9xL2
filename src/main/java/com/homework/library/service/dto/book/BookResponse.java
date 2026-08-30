package com.homework.library.service.dto.book;

import com.homework.library.service.entity.Book;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private Long borrowerId;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .borrowerId(
                        book.getBorrower() != null
                                ? book.getBorrower().getId()
                                : null)
                .build();
    }
}
