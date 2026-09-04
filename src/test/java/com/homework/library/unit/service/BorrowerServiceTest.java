package com.homework.library.unit.service;

import com.homework.library.service.dto.borrower.BorrowedBooksResponse;
import com.homework.library.service.dto.borrower.BorrowerResponse;
import com.homework.library.service.dto.borrower.CreateBorrowerRequest;
import com.homework.library.service.entity.Book;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.exception.BorrowerNotFoundException;
import com.homework.library.service.repository.BorrowerRepository;
import com.homework.library.service.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowerServiceTest {

    private static final Long AVAILABLE_BOOK_ID = 1L;
    private static final Long BORROWER_ID = 1L;
    private static final String BOOK_TITLE_1 = "Meditations";
    private static final String BOOK_AUTHOR_1 = "Marcus Aurelius";
    private static final String BORROWER = "Adam Smith";

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    private Book book;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        borrower = Borrower.builder()
                .id(BORROWER_ID)
                .borrower(BORROWER)
                .borrowedBooks(new ArrayList<>())
                .build();
        book = Book.builder()
                .id(1L)
                .title(BOOK_TITLE_1)
                .author(BOOK_AUTHOR_1)
                .borrower(borrower)
                .build();
    }

    @Test
    void getBorrower_shouldReturnBorrowerWithoutBorrowedBooks() {
        // Given
        when(borrowerRepository.findById(BORROWER_ID))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowerResponse result = borrowerService.getBorrower(BORROWER_ID);

        // Then
        assertThat(result.getId()).isEqualTo(BORROWER_ID);
        assertThat(result.getBorrower()).isEqualTo(BORROWER);
        verify(borrowerRepository).findById(BORROWER_ID);
    }

    @Test
    void getBorrower_shouldThrowExceptionWhenBorrowerNotFound() {
        // When
        when(borrowerRepository.findById(BORROWER_ID))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> borrowerService.getBorrower(BORROWER_ID))
                .isInstanceOf(BorrowerNotFoundException.class);
        verify(borrowerRepository).findById(BORROWER_ID);
    }

    @Test
    void getBorrowedBooks_shouldReturnBorrowedBooksForBorrower() {
        // Given
        borrower.getBorrowedBooks().add(book);
        when(borrowerRepository.findById(BORROWER_ID))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowedBooksResponse result = borrowerService.getBorrowedBooks(BORROWER_ID);

        // Then
        assertThat(result.getBooks()).hasSize(1);
        assertThat(result.getBooks().getFirst().getId()).isEqualTo(AVAILABLE_BOOK_ID);
        assertThat(result.getBooks().getFirst().getTitle()).isEqualTo(BOOK_TITLE_1);
        assertThat(result.getBooks().getFirst().getAuthor()).isEqualTo(BOOK_AUTHOR_1);
        assertThat(result.getBooks().getFirst().getBorrowerId()).isEqualTo(BORROWER_ID);
        verify(borrowerRepository).findById(BORROWER_ID);
    }

    @Test
    void getBorrowedBooks_shouldReturnEmptyListWhenBorrowerHasNoBooks() {
        // Given
        when(borrowerRepository.findById(BORROWER_ID))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowedBooksResponse result = borrowerService.getBorrowedBooks(BORROWER_ID);

        // Then
        assertThat(result.getBooks()).isEmpty();
        verify(borrowerRepository).findById(BORROWER_ID);
    }

    @Test
    void getBorrowedBooks_shouldThrowExceptionWhenBorrowerNotFound() {
        // Given
        when(borrowerRepository.findById(BORROWER_ID))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> borrowerService.getBorrowedBooks(BORROWER_ID))
                .isInstanceOf(BorrowerNotFoundException.class);
        verify(borrowerRepository).findById(BORROWER_ID);
    }

    @Test
    void createBorrower_shouldCreateBorrower() {
        // Given
        CreateBorrowerRequest request = new CreateBorrowerRequest(BORROWER);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        // When
        BorrowerResponse result = borrowerService.createBorrower(request);

        // Then
        assertThat(result.getId()).isEqualTo(BORROWER_ID);
        assertThat(result.getBorrower()).isEqualTo(BORROWER);
        verify(borrowerRepository).save(any(Borrower.class));
    }
}
