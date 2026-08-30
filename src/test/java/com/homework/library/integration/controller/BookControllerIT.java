package com.homework.library.integration.controller;

import com.homework.library.integration.BaseIntegration;
import com.homework.library.service.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerIT extends BaseIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldReturnAvailableBooks() throws Exception {
        // Given
//        Book book = Book.builder()
//                .title("Meditations")
//                .author("Marcus Aurelius")
//                .borrower(null)
//                .build();

        // When
//        bookRepository.save(book);

        // Then
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("A Game of Thrones")))
                .andExpect(jsonPath("$[0].author", is("George R. R. Martin")))
                .andExpect(jsonPath("$[0].borrowerId").doesNotExist())
                .andExpect(jsonPath("$[1].title", is("The Godfather")))
                .andExpect(jsonPath("$[1].author", is("Mario Puzo")))
                .andExpect(jsonPath("$[1].borrowerId").doesNotExist());
    }
}
