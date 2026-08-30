package com.homework.library.service;

import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.entity.Book;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.repository.BookRepository;
import com.homework.library.service.service.BookService;
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
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookService bookService;

	@Test
	void shouldReturnAllBooks() {
		// Given
		Borrower borrower = Borrower.builder()
				.id(1L)
				.borrower("Adam Smith")
				.build();

		Book book = Book.builder()
				.id(1L)
				.title("Meditations")
				.author("Marcus Aurelius")
				.borrower(borrower)
				.build();

		// When
		when(bookRepository.findAll()).thenReturn(List.of(book));
		List<BookResponse> result = bookService.getBooks();

		// Then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(1L);
		assertThat(result.getFirst().getTitle()).isEqualTo("Meditations");
		assertThat(result.getFirst().getAuthor()).isEqualTo("Marcus Aurelius");
		assertThat(result.getFirst().getBorrowerId()).isEqualTo(1L);
		verify(bookRepository).findAll();
	}

}
