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

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerService borrowerService;

    private Book book;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        borrower = Borrower.builder()
                .id(1L)
                .borrower("Adam Smith")
                .borrowedBooks(new ArrayList<>())
                .build();
        book = Book.builder()
                .id(1L)
                .title("Meditations")
                .author("Marcus Aurelius")
                .borrower(borrower)
                .build();
    }

    @Test
    void getBorrower_shouldReturnBorrowerWithoutBorrowedBooks() {
        // Given
        when(borrowerRepository.findById(1L))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowerResponse result = borrowerService.getBorrower(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBorrower()).isEqualTo("Adam Smith");
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void getBorrower_shouldThrowExceptionWhenBorrowerNotFound() {
        // When
        when(borrowerRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> borrowerService.getBorrower(1L))
                .isInstanceOf(BorrowerNotFoundException.class);
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void getBorrowedBooks_shouldReturnBorrowedBooksForBorrower() {
        // Given
        borrower.getBorrowedBooks().add(book);
        when(borrowerRepository.findById(1L))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowedBooksResponse result = borrowerService.getBorrowedBooks(1L);

        // Then
        assertThat(result.getBooks()).hasSize(1);
        assertThat(result.getBooks().getFirst().getId()).isEqualTo(1L);
        assertThat(result.getBooks().getFirst().getTitle()).isEqualTo("Meditations");
        assertThat(result.getBooks().getFirst().getAuthor()).isEqualTo("Marcus Aurelius");
        assertThat(result.getBooks().getFirst().getBorrowerId()).isEqualTo(1L);
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void getBorrowedBooks_shouldReturnEmptyListWhenBorrowerHasNoBooks() {
        // Given
        when(borrowerRepository.findById(1L))
                .thenReturn(Optional.of(borrower));

        // When
        BorrowedBooksResponse result = borrowerService.getBorrowedBooks(1L);

        // Then
        assertThat(result.getBooks()).isEmpty();
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void getBorrowedBooks_shouldThrowExceptionWhenBorrowerNotFound() {
        // Given
        when(borrowerRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> borrowerService.getBorrowedBooks(1L))
                .isInstanceOf(BorrowerNotFoundException.class);
        verify(borrowerRepository).findById(1L);
    }

    @Test
    void createBorrower_shouldCreateBorrower() {
        // Given
        CreateBorrowerRequest request = new CreateBorrowerRequest("Adam Smith");
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        // When
        BorrowerResponse result = borrowerService.createBorrower(request);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBorrower()).isEqualTo("Adam Smith");
        verify(borrowerRepository).save(any(Borrower.class));
    }

}
