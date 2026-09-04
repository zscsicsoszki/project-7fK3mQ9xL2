package com.homework.library.service.controller;

import com.homework.library.service.dto.borrower.BorrowedBooksResponse;
import com.homework.library.service.dto.borrower.BorrowerResponse;
import com.homework.library.service.dto.borrower.CreateBorrowerRequest;
import com.homework.library.service.service.BorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrowers")
@RequiredArgsConstructor
public class BorrowerController {

    private final BorrowerService borrowerService;

    @Operation(summary = "Registers a new borrower")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowerResponse createBorrower(@RequestBody CreateBorrowerRequest request) {
        return borrowerService.createBorrower(request);
    }

    @Operation(summary = "Retrieves details of a specific borrower")
    @GetMapping("/{id}")
    public BorrowerResponse getBorrower(@PathVariable Long id) {
        return borrowerService.getBorrower(id);
    }

    @Operation(summary = "Retrieves the list of books borrowed by a specific borrower")
    @GetMapping("/{id}/books")
    public BorrowedBooksResponse getBorrowedBooks(@PathVariable Long id) {
        return borrowerService.getBorrowedBooks(id);
    }
}
