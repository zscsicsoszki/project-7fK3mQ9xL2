package com.homework.library.service.dto.borrower;

import com.homework.library.service.dto.book.BookResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BorrowedBooksResponse {

    private List<BookResponse> books;
}
