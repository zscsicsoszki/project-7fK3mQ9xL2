package com.homework.library.unit.service;

import com.homework.library.service.dto.book.AddBookRequest;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.entity.Book;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.exception.BookAlreadyBorrowedException;
import com.homework.library.service.exception.BookNotFoundException;
import com.homework.library.service.exception.BorrowerNotFoundException;
import com.homework.library.service.repository.BookRepository;
import com.homework.library.service.repository.BorrowerRepository;
import com.homework.library.service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final Long AVAILABLE_BOOK_ID = 1L;
    private static final Long UNAVAILABLE_BOOK_ID = 2L;
    private static final Long BORROWER_ID = 1L;
    private static final String BOOK_TITLE_1 = "Meditations";
    private static final String BOOK_TITLE_2 = "Dune";
    private static final String BOOK_AUTHOR_1 = "Marcus Aurelius";
    private static final String BOOK_AUTHOR_2 = "Frank Herbert";

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BookService bookService;

    private Book book_1;
    private Book book_2;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        borrower = Borrower.builder()
                .id(1L)
                .borrower("Adam Smith")
                .build();
        book_1 = Book.builder()
                .id(1L)
                .title(BOOK_TITLE_1)
                .author(BOOK_AUTHOR_1)
                .borrower(null)
                .build();
        book_2 = Book.builder()
                .id(2L)
                .title(BOOK_TITLE_2)
                .author(BOOK_AUTHOR_2)
                .borrower(borrower)
                .build();
    }

    @Test
    void getBooks_shouldReturnAllAvailableBooks() {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of(book_1, book_2));

        // When
        List<BookResponse> result = bookService.getBooks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(AVAILABLE_BOOK_ID);
        assertThat(result.getFirst().getTitle()).isEqualTo(BOOK_TITLE_1);
        assertThat(result.getFirst().getAuthor()).isEqualTo(BOOK_AUTHOR_1);
        assertThat(result.getFirst().getBorrowerId()).isNull();
        verify(bookRepository).findAll();
    }

    @Test
    void addBook_shouldAddNewBook() {
        // Given
        AddBookRequest addBookRequest = new AddBookRequest(BOOK_TITLE_1, BOOK_AUTHOR_1);
        when(bookRepository.save(any(Book.class))).thenReturn(book_1);

        // When
        BookResponse result = bookService.addBook(addBookRequest);

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo(BOOK_TITLE_1);
        assertThat(result.getAuthor()).isEqualTo(BOOK_AUTHOR_1);
        assertThat(result.getBorrowerId()).isNull();
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void borrowBook_shouldBorrowBookWhenBookAndBorrowerExists() {
        // Given
        when(bookRepository.findById(AVAILABLE_BOOK_ID)).thenReturn(Optional.ofNullable(book_1));
        when(bookRepository.save(any(Book.class))).thenReturn(book_1);
        when(borrowerRepository.findById(borrower.getId())).thenReturn(Optional.of(borrower));

        // When
        BookResponse result = bookService.borrowBook(AVAILABLE_BOOK_ID, BORROWER_ID);

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo(BOOK_TITLE_1);
        assertThat(result.getAuthor()).isEqualTo(BOOK_AUTHOR_1);
        assertThat(result.getBorrowerId()).isEqualTo(BORROWER_ID);
        verify(bookRepository).findById(AVAILABLE_BOOK_ID);
        verify(bookRepository).save(any(Book.class));
        verify(borrowerRepository).findById(borrower.getId());
    }

    @Test
    void borrowBook_shouldReturnBookWhenBookExistsButNoBorrower() {
        // Given
        when(bookRepository.findById(2L)).thenReturn(Optional.ofNullable(book_2));
        when(bookRepository.save(any(Book.class))).thenReturn(book_2);

        // When
        BookResponse result = bookService.borrowBook(UNAVAILABLE_BOOK_ID, BORROWER_ID);

        // Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo(BOOK_TITLE_2);
        assertThat(result.getAuthor()).isEqualTo(BOOK_AUTHOR_2);
        assertThat(result.getBorrowerId()).isNull();
        verify(bookRepository).findById(2L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void borrowBook_shouldThrowBookNotFoundExceptionWhenBookDoesNotExist() {
        // Given
        when(bookRepository.findById(AVAILABLE_BOOK_ID)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> bookService.borrowBook(AVAILABLE_BOOK_ID, BORROWER_ID))
                .isInstanceOf(BookNotFoundException.class);
        verify(bookRepository).findById(AVAILABLE_BOOK_ID);
        verifyNoInteractions(borrowerRepository);
    }

    @Test
    void borrowBook_shouldThrowBorrowerNotFoundExceptionWhenBorrowerDoesNotExist() {
        // Given
        when(bookRepository.findById(AVAILABLE_BOOK_ID)).thenReturn(Optional.of(book_1));
        when(borrowerRepository.findById(BORROWER_ID)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> bookService.borrowBook(AVAILABLE_BOOK_ID, BORROWER_ID))
                .isInstanceOf(BorrowerNotFoundException.class);
        verify(bookRepository).findById(AVAILABLE_BOOK_ID);
        verify(borrowerRepository).findById(BORROWER_ID);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenBookIsAlreadyBorrowedByAnotherBorrower() {
        // Given
        Long requestedBorrowerId = 3L;
        when(bookRepository.findById(UNAVAILABLE_BOOK_ID)).thenReturn(Optional.of(book_2));

        // Then
        assertThatThrownBy(() -> bookService.borrowBook(UNAVAILABLE_BOOK_ID, requestedBorrowerId))
                .isInstanceOf(BookAlreadyBorrowedException.class);

        verify(bookRepository).findById(UNAVAILABLE_BOOK_ID);
        verify(bookRepository, never()).save(any(Book.class));
        verifyNoInteractions(borrowerRepository);
    }
}
