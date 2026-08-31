package com.homework.library.unit.service;

import com.homework.library.service.dto.book.AddBookRequest;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.entity.Book;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.repository.BookRepository;
import com.homework.library.service.repository.BorrowerRepository;
import com.homework.library.service.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	private static final Borrower BORROWER = Borrower.builder()
			.id(1L)
			.borrower("Adam Smith")
			.build();
	private static final Book AVAILABLE_BOOK_1 = Book.builder()
			.id(1L)
			.title("Meditations")
			.author("Marcus Aurelius")
			.borrower(null)
			.build();
	private static final Book AVAILABLE_BOOK_2 = Book.builder()
			.id(2L)
			.title("Dune")
			.author("Frank Herbert")
			.borrower(BORROWER)
			.build();
	private static final Book AVAILABLE_BOOK_3 = Book.builder()
			.id(1L)
			.title("The Godfather")
			.author("Mario Puzo")
			.borrower(null)
			.build();

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BorrowerRepository borrowerRepository;

	@InjectMocks
	private BookService bookService;

	@Test
	void shouldReturnAllAvailableBooks() {
		// Given
		when(bookRepository.findAll()).thenReturn(List.of(AVAILABLE_BOOK_1, AVAILABLE_BOOK_2));

		// When
		List<BookResponse> result = bookService.getBooks();

		// Then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(1L);
		assertThat(result.getFirst().getTitle()).isEqualTo("Meditations");
		assertThat(result.getFirst().getAuthor()).isEqualTo("Marcus Aurelius");
		assertThat(result.getFirst().getBorrowerId()).isNull();
		verify(bookRepository).findAll();
	}

	@Test
	void shouldAddNewBook() {
		// Given
		AddBookRequest addBookRequest = new AddBookRequest("Meditations", "Marcus Aurelius");
		when(bookRepository.save(any(Book.class))).thenReturn(AVAILABLE_BOOK_1);

		// When
		BookResponse result = bookService.addBook(addBookRequest);

		// Then
		assertThat(result.getId()).isNotNull();
		assertThat(result.getTitle()).isEqualTo("Meditations");
		assertThat(result.getAuthor()).isEqualTo("Marcus Aurelius");
		assertThat(result.getBorrowerId()).isNull();
		verify(bookRepository).save(any(Book.class));
	}

	@Test
	void shouldBorrowBookWhenBookAndBorrowerExists() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(AVAILABLE_BOOK_1));
		when(bookRepository.save(any(Book.class))).thenReturn(AVAILABLE_BOOK_1);
		when(borrowerRepository.findById(BORROWER.getId())).thenReturn(Optional.of(BORROWER));

		// When
		BookResponse result = bookService.borrowBook(1L ,1L);

		// Then
		assertThat(result.getId()).isNotNull();
		assertThat(result.getTitle()).isEqualTo("Meditations");
		assertThat(result.getAuthor()).isEqualTo("Marcus Aurelius");
		assertThat(result.getBorrowerId()).isEqualTo(1L);

		verify(bookRepository).findById(1L);
		verify(bookRepository).save(any(Book.class));
		verify(borrowerRepository).findById(BORROWER.getId());
	}

	// TODO: Add UT for other borrower case (setting borrower to null)

}
