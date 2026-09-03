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
        // Given
        Long bookId = 1L;
        Long borrowerId = 1L;

        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", bookId)
                        .param("borrower_id", borrowerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("A Game of Thrones")))
                .andExpect(jsonPath("$.author", is("George R. R. Martin")))
                .andExpect(jsonPath("$.borrowerId", is(1)));

        // Then
        Book book = bookRepository.findById(bookId)
                .orElseThrow();
        assertThat(book.getBorrower()).isNotNull();
        assertThat(book.getBorrower().getId()).isEqualTo(borrowerId);
    }


    @Test
    void shouldReturnBorrowedBookToAvailable() throws Exception {
        // Given
        Long bookId = 2L;
        Long borrowerId = 1L;

        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", bookId)
                        .param("borrower_id", borrowerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("The Godfather")))
                .andExpect(jsonPath("$.author", is("Mario Puzo")))
                .andExpect(jsonPath("$.borrowerId").doesNotExist());

        // Then
        Book book = bookRepository.findById(bookId)
                .orElseThrow();
        assertThat(book.getBorrower()).isNull();
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        // Given
        Long bookId = 999L;
        Long borrowerId = 1L;

        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", bookId)
                .param("borrower_id", borrowerId.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Requested book is not found.")));

        // Then
        assertThat(bookRepository.findById(bookId)).isEmpty();
    }


    @Test
    void shouldReturnNotFoundWhenBorrowerDoesNotExist() throws Exception {
        // Given
        Long bookId = 1L;
        Long borrowerId = 999L;

        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", bookId)
                .param("borrower_id", borrowerId.toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Requested borrower is not found.")));

        // Then
        Book book = bookRepository.findById(bookId)
            .orElseThrow();
        assertThat(book.getBorrower()).isNull();
    }


    @Test
    void shouldReturnConflictWhenBookIsAlreadyBorrowedByAnotherBorrower() throws Exception {
        // Given
        Long bookId = 2L;
        Long borrowerId = 2L;

        // When / Then
        mockMvc.perform(patch("/books/{id}/borrow", bookId)
                .param("borrower_id", borrowerId.toString()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message",
                is("Book with id 2 is already borrowed by another borrower with id 2")));

        // Then
        Book book = bookRepository.findById(bookId)
            .orElseThrow();
        assertThat(book.getBorrower()).isNotNull();
        assertThat(book.getBorrower().getId()).isEqualTo(1L);
    }
}
