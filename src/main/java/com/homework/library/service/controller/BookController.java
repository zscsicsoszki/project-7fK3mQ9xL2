package com.homework.library.service.controller;

import com.homework.library.service.dto.book.AddBookRequest;
import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Retrieve a list of all available books")
    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getBooks();
    }

    @Operation(summary = "Adds a new book to the library")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse addBook(@RequestBody AddBookRequest request) {
        return bookService.addBook(request);
    }

    @Operation(summary = "Borrows a book")
    @PatchMapping("/{id}/borrow")
    public BookResponse borrowBook(
            @PathVariable Long id,
            @RequestParam(name = "borrower_id") Long borrowerId
    ) {
        return bookService.borrowBook(id, borrowerId);
    }
}
