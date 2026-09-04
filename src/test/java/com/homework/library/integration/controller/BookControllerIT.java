package com.homework.library.integration.controller;

import com.homework.library.integration.BaseIntegration;
import com.homework.library.service.entity.Book;
import com.homework.library.service.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerIT extends BaseIntegration {

    private static final Long BOOK_ID_WITHOUT_BORROWER = 1L;
    private static final Long BOOK_ID_WITH_BORROWER = 2L;
    private static final Long BOOK_DOES_NOT_EXIST = 100L;
    private static final Long BORROWER_ID_WITHOUT_BOOKS = 1L;
    private static final Long BORROWER_ID_WITH_BOOKS = 2L;
    private static final Long BORROWER_DOES_NOT_EXIST = 100L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldReturnAvailableBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("A Game of Thrones")))
                .andExpect(jsonPath("$[0].author", is("George R. R. Martin")))
                .andExpect(jsonPath("$[0].borrowerId").doesNotExist());
    }

    @Test
    void shouldAddBook() throws Exception {
        String requestBody = """
                {
                    "title": "The Godfather",
                    "author": "Mario Puzo"
                }
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title", is("The Godfather")))
                .andExpect(jsonPath("$.author", is("Mario Puzo")))
                .andExpect(jsonPath("$.borrowerId").doesNotExist());

        assertThat(bookRepository.findAll())
                .anyMatch(book ->
                        book.getTitle().equals("The Godfather")
                                && book.getAuthor().equals("Mario Puzo")
                                && book.getBorrower() == null);
    }

    @Test
    void shouldBorrowAvailableBook() throws Exception {
        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", BOOK_ID_WITHOUT_BORROWER)
                        .param("borrower_id", BORROWER_ID_WITHOUT_BOOKS.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("A Game of Thrones")))
                .andExpect(jsonPath("$.author", is("George R. R. Martin")))
                .andExpect(jsonPath("$.borrowerId", is(1)));

        // Then
        Book book = bookRepository.findById(BOOK_ID_WITHOUT_BORROWER)
                .orElseThrow();
        assertThat(book.getBorrower()).isNotNull();
        assertThat(book.getBorrower().getId()).isEqualTo(BORROWER_ID_WITHOUT_BOOKS);
    }


    @Test
    void shouldReturnBorrowedBookToAvailable() throws Exception {
        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", BOOK_ID_WITH_BORROWER)
                        .param("borrower_id", BORROWER_ID_WITHOUT_BOOKS.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("The Godfather")))
                .andExpect(jsonPath("$.author", is("Mario Puzo")))
                .andExpect(jsonPath("$.borrowerId").doesNotExist());

        // Then
        Book book = bookRepository.findById(BOOK_ID_WITH_BORROWER)
                .orElseThrow();
        assertThat(book.getBorrower()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", BOOK_DOES_NOT_EXIST)
                .param("borrower_id", BORROWER_ID_WITHOUT_BOOKS.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Requested book is not found.")));

        // Then
        assertThat(bookRepository.findById(BOOK_DOES_NOT_EXIST)).isEmpty();
    }


    @Test
    void shouldReturnNotFoundWhenBorrowerDoesNotExist() throws Exception {
        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", BOOK_ID_WITHOUT_BORROWER)
                .param("borrower_id", BORROWER_DOES_NOT_EXIST.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Requested borrower is not found.")));

        // Then
        Book book = bookRepository.findById(BOOK_ID_WITHOUT_BORROWER)
            .orElseThrow();
        assertThat(book.getBorrower()).isNull();
    }


    @Test
    void shouldReturnConflictWhenBookIsAlreadyBorrowedByAnotherBorrower() throws Exception {
        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", BOOK_ID_WITH_BORROWER)
                .param("borrower_id", BORROWER_ID_WITH_BOOKS.toString()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message",
                is("Book with id 2 is already borrowed by someone.")));

        // Then
        Book book = bookRepository.findById(BOOK_ID_WITH_BORROWER)
            .orElseThrow();
        assertThat(book.getBorrower()).isNotNull();
        assertThat(book.getBorrower().getId()).isEqualTo(BORROWER_ID_WITHOUT_BOOKS);
    }
}
