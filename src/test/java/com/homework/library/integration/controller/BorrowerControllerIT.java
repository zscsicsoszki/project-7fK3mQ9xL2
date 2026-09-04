package com.homework.library.integration.controller;

import com.homework.library.integration.BaseIntegration;
import com.homework.library.service.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BorrowerControllerIT extends BaseIntegration {

    private static final Long BORROWER_ID_WITH_BOOKS = 1L;
    private static final Long BORROWER_ID_WITHOUT_BOOKS = 2L;
    private static final Long BORROWER_DOES_NOT_EXIST = 100L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BorrowerRepository borrowerRepository;

    @Test
    void createBorrower_shouldCreateBorrower() throws Exception {
        // Given
        String requestBody = """
            {
                "borrower": "Michael Scott"
            }
            """;

        // When / Then
        mockMvc.perform(post("/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.borrower", is("Michael Scott")));

        // Then
        assertThat(borrowerRepository.findAll())
            .anyMatch(borrower ->
                borrower.getBorrower().equals("Michael Scott"));
    }

    @Test
    void getBorrower_shouldReturnBorrowerWhenExists() throws Exception {
        // When / Then
        mockMvc.perform(get("/borrowers/{id}", BORROWER_ID_WITH_BOOKS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.borrower", is("Adam Smith")));
    }

    @Test
    void getBorrower_shouldReturnNotFoundWhenBorrowerDoesNotExist() throws Exception {
        // When / Then
        mockMvc.perform(get("/borrowers/{id}", BORROWER_DOES_NOT_EXIST))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message",
                is("Requested borrower is not found.")));

        // Then
        assertThat(borrowerRepository.findById(BORROWER_DOES_NOT_EXIST))
            .isEmpty();
    }

    @Test
    void getBorrowedBooks_shouldReturnBorrowedBooksForBorrower() throws Exception {
        // When / Then
        mockMvc.perform(get("/borrowers/{id}/books", BORROWER_ID_WITH_BOOKS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books", hasSize(1)))
            .andExpect(jsonPath("$.books[0].id", is(2)))
            .andExpect(jsonPath("$.books[0].title", is("The Godfather")))
            .andExpect(jsonPath("$.books[0].author", is("Mario Puzo")))
            .andExpect(jsonPath("$.books[0].borrowerId", is(1)));
    }

    @Test
    void getBorrowedBooks_shouldReturnEmptyListWhenBorrowerHasNoBooks() throws Exception {
        // When / Then
        mockMvc.perform(get("/borrowers/{id}/books", BORROWER_ID_WITHOUT_BOOKS))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books", hasSize(0)));
    }

    @Test
    void getBorrowedBooks_shouldReturnNotFoundWhenBorrowerDoesNotExist() throws Exception {
        // When / Then
        mockMvc.perform(get("/borrowers/{id}/books", BORROWER_DOES_NOT_EXIST))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message",
                is("Requested borrower is not found.")));

        // Then
        assertThat(borrowerRepository.findById(BORROWER_DOES_NOT_EXIST))
            .isEmpty();
    }
}
