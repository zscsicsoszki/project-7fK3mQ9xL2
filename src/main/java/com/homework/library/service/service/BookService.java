package com.homework.library.service.service;

import com.homework.library.service.dto.book.AddBookRequest;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.entity.Book;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.exception.BookAlreadyBorrowedException;
import com.homework.library.service.exception.BookNotFoundException;
import com.homework.library.service.exception.BorrowerNotFoundException;
import com.homework.library.service.repository.BookRepository;
import com.homework.library.service.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public List<BookResponse> getBooks() {
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getBorrower() == null)
                .map(BookResponse::from)
                .toList();
    }

    public BookResponse addBook(AddBookRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .build();

        return BookResponse.from(bookRepository.save(book));
    }

    public BookResponse borrowBook(Long id, Long borrowerId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
        if (book.getBorrower() == null) {
            Borrower borrower = borrowerRepository.findById(borrowerId)
                    .orElseThrow(BorrowerNotFoundException::new);

            book.setBorrower(borrower);
        } else if (!book.getBorrower().getId().equals(borrowerId)) {
            throw new BookAlreadyBorrowedException(book.getId(), borrowerId);
        } else {
            book.setBorrower(null);
        }

        return BookResponse.from(bookRepository.save(book));
    }
}
