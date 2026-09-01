package com.homework.library.unit.controller;

import com.homework.library.service.controller.BookController;
import com.homework.library.service.dto.book.AddBookRequest;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private static final Long BOOK_ID = 1L;
    private static final Long BORROWER_ID = 1L;
    private static final String BOOK_TITLE = "Meditations";
    private static final String BOOK_AUTHOR = "Marcus Aurelius";

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private BookResponse bookResponse;
    private AddBookRequest addBookRequest;

    @BeforeEach
    void setUp() {
        bookResponse = BookResponse.builder()
                .id(BOOK_ID)
                .title(BOOK_TITLE)
                .author(BOOK_AUTHOR)
                .borrowerId(null)
                .build();
        addBookRequest = new AddBookRequest(
                BOOK_TITLE,
                BOOK_AUTHOR
        );
    }

    @Test
    void getBooks_shouldReturnAvailableBooks() {
        // Given
        List<BookResponse> books = List.of(bookResponse);
        when(bookService.getBooks()).thenReturn(books);

        // When
        List<BookResponse> result = bookController.getBooks();

        // Then
        assertThat(result).hasSize(1).containsExactly(bookResponse);
        verify(bookService).getBooks();
    }

    @Test
    void addBook_shouldAddBook() {
        // Given
        when(bookService.addBook(addBookRequest)).thenReturn(bookResponse);

        // When
        BookResponse result = bookController.addBook(addBookRequest);

        // Then
        assertThat(result).isSameAs(bookResponse);
        verify(bookService).addBook(addBookRequest);
    }

    @Test
    void borrowBook_shouldBorrowBook() {
        // Given
        BookResponse borrowedBook = BookResponse.builder()
                .id(BOOK_ID)
                .title(BOOK_TITLE)
                .author(BOOK_AUTHOR)
                .borrowerId(BORROWER_ID)
                .build();

        when(bookService.borrowBook(BOOK_ID, BORROWER_ID)).thenReturn(borrowedBook);

        // When
        BookResponse result = bookController.borrowBook(BOOK_ID, BORROWER_ID);

        // Then
        assertThat(result).isSameAs(borrowedBook);
        verify(bookService).borrowBook(BOOK_ID, BORROWER_ID);
    }
}
