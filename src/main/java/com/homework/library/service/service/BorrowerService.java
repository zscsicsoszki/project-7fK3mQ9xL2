package com.homework.library.service.service;

import com.homework.library.service.dto.book.BookResponse;
import com.homework.library.service.dto.borrower.BorrowedBooksResponse;
import com.homework.library.service.dto.borrower.BorrowerResponse;
import com.homework.library.service.dto.borrower.CreateBorrowerRequest;
import com.homework.library.service.entity.Borrower;
import com.homework.library.service.exception.BorrowerNotFoundException;
import com.homework.library.service.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerResponse createBorrower(CreateBorrowerRequest request) {
        Borrower borrower = Borrower.builder()
                .borrower(request.getBorrower())
                .build();

        borrower = borrowerRepository.save(borrower);

        return BorrowerResponse.builder()
                .id(borrower.getId())
                .borrower(borrower.getBorrower())
                .build();
    }

    public BorrowerResponse getBorrower(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(BorrowerNotFoundException::new);

        return BorrowerResponse.builder()
                .id(borrower.getId())
                .borrower(borrower.getBorrower())
                .build();
    }

    public BorrowedBooksResponse getBorrowedBooks(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(BorrowerNotFoundException::new);

        return BorrowedBooksResponse.builder()
                .books(borrower.getBorrowedBooks().stream()
                        .map(BookResponse::from)
                        .toList())
                .build();
    }
}
