package com.homework.library.unit.controller;

import com.homework.library.service.controller.BorrowerController;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.dto.borrower.BorrowedBooksResponse;
import com.homework.library.service.dto.borrower.BorrowerResponse;
import com.homework.library.service.dto.borrower.CreateBorrowerRequest;
import com.homework.library.service.service.BorrowerService;
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
public class BorrowerControllerTest {

    private static final Long BOOK_ID = 1L;
    private static final Long BORROWER_ID = 1L;
    private static final String BOOK_TITLE = "Meditations";
    private static final String BOOK_AUTHOR = "Marcus Aurelius";
    private static final String BORROWER = "Adam Smith";

    @Mock
    private BorrowerService borrowerService;

    @InjectMocks
    private BorrowerController borrowerController;

    private BorrowerResponse borrowerResponse;
    private CreateBorrowerRequest createBorrowerRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        borrowerResponse = BorrowerResponse.builder()
                .id(BORROWER_ID)
                .borrower(BORROWER)
                .build();
        createBorrowerRequest = new CreateBorrowerRequest(BORROWER);
        bookResponse = BookResponse.builder()
                .id(BOOK_ID)
                .title(BOOK_TITLE)
                .author(BOOK_AUTHOR)
                .borrowerId(BORROWER_ID)
                .build();
    }

    @Test
    void createBorrower_shouldCreateBorrower() {
        // Given
        when(borrowerService.createBorrower(createBorrowerRequest)).thenReturn(borrowerResponse);

        // When
        BorrowerResponse result = borrowerController.createBorrower(createBorrowerRequest);

        // Then
        assertThat(result).isSameAs(borrowerResponse);
        verify(borrowerService).createBorrower(createBorrowerRequest);
    }

    @Test
    void getBorrower_shouldReturnBorrower() {
        // Given
        when(borrowerService.getBorrower(BORROWER_ID)).thenReturn(borrowerResponse);

        // When
        BorrowerResponse result = borrowerController.getBorrower(BORROWER_ID);

        // Then
        assertThat(result).isSameAs(borrowerResponse);
        verify(borrowerService).getBorrower(BORROWER_ID);
    }

    @Test
    void getBorrowedBooks_shouldReturnBorrowedBooks() {
        // Given
        BorrowedBooksResponse borrowedBooksResponse = BorrowedBooksResponse.builder()
                .books(List.of(bookResponse))
                .build();
        when(borrowerService.getBorrowedBooks(BORROWER_ID)).thenReturn(borrowedBooksResponse);

        // When
        BorrowedBooksResponse result = borrowerController.getBorrowedBooks(BORROWER_ID);

        // Then
        assertThat(result).isSameAs(borrowedBooksResponse);
        assertThat(result.getBooks()).containsExactly(bookResponse);
        verify(borrowerService).getBorrowedBooks(BORROWER_ID);
    }
}
